/*
 * libmad - MPEG audio decoder library
 * Copyright (C) 2000-2004 Underbit Technologies, Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: minimad.c,v 1.4 2004/01/23 09:41:32 rob Exp $
 */
#include  <jni.h>
#include  <android/log.h>

# include <stdio.h>
# include <unistd.h>
# include <sys/stat.h>
# include <sys/mman.h>
#include <stdlib.h>

# include "mad.h"



#define TAG "libMad"



static FILE * pmp3File = NULL;
static FILE * ppcmFile = NULL;

static int decode(unsigned char const *, unsigned long);


JNIEXPORT jboolean

JNICALL
Java_csh_tiro_cc_Libmad_decodeFile(JNIEnv * env, jobject obj,jstring mp3File,jstring pcmFile)
{

    //检查参数
    if(mp3File == NULL || pcmFile == NULL){
        __android_log_print(ANDROID_LOG_ERROR,TAG,"param error(NULL)...");
        return JNI_FALSE;
    }

    char * cPathMp3 = (char *)(*env)->GetStringUTFChars(env,mp3File,NULL);
    char * cPathPcm = (char *)(*env)->GetStringUTFChars(env,pcmFile,NULL);


    pmp3File = fopen(cPathMp3,"r");
    if(pmp3File==NULL)
    {
        __android_log_print(ANDROID_LOG_ERROR,TAG,"error open mp3 file...");
        (*env)->ReleaseStringUTFChars(env, mp3File, cPathMp3);
        (*env)->ReleaseStringUTFChars(env, pcmFile, cPathPcm);
        return JNI_FALSE;
    }
    ppcmFile = fopen(cPathPcm,"wb");
    if(ppcmFile == NULL)
    {
        fclose(pmp3File);
        __android_log_print(ANDROID_LOG_ERROR,TAG,"error create out file...");
        (*env)->ReleaseStringUTFChars(env, mp3File, cPathMp3);
        (*env)->ReleaseStringUTFChars(env, pcmFile, cPathPcm);
        return JNI_FALSE;
    }
    //获取原始数据的长度
    fseek(pmp3File,0,SEEK_END);
    long srcFileSize = ftell(pmp3File);
    fseek(pmp3File,0,SEEK_SET);

    //创建输入缓冲
    void * mp3Data = malloc(srcFileSize);
    if(!mp3Data)
    {
        fclose(ppcmFile);
        fclose(pmp3File);
        __android_log_print(ANDROID_LOG_ERROR,TAG,"not enough memory to alloc...");
        (*env)->ReleaseStringUTFChars(env, mp3File, cPathMp3);
        (*env)->ReleaseStringUTFChars(env, pcmFile, cPathPcm);
        return JNI_FALSE;
    }
    //读取数据
    fread(mp3Data,srcFileSize,1,pmp3File);

    //执行解码
    decode(mp3Data, srcFileSize);
    //清理资源
    fclose(ppcmFile);
    fclose(pmp3File);
    free(mp3Data);
    (*env)->ReleaseStringUTFChars(env, mp3File, cPathMp3);
    (*env)->ReleaseStringUTFChars(env, pcmFile, cPathPcm);
    //返回数据
    return JNI_TRUE;

}



struct buffer {
  unsigned char const *start;
  unsigned long length;
};



static enum mad_flow input(
        void *data,
        struct mad_stream *stream)
{
  struct buffer * buffer = data;

  if (!buffer->length)
    return MAD_FLOW_STOP;

  mad_stream_buffer(stream, buffer->start, buffer->length);

  buffer->length = 0;

  return MAD_FLOW_CONTINUE;
}

/*
 * The following utility routine performs simple rounding, clipping, and
 * scaling of MAD's high-resolution samples down to 16 bits. It does not
 * perform any dithering or noise shaping, which would be recommended to
 * obtain any exceptional audio quality. It is therefore not recommended to
 * use this routine if high-quality output is desired.
 */

static inline
signed int scale(mad_fixed_t sample)
{
  /* round */
  sample += (1L << (MAD_F_FRACBITS - 16));

  /* clip */
  if (sample >= MAD_F_ONE)
    sample = MAD_F_ONE - 1;
  else if (sample < -MAD_F_ONE)
    sample = -MAD_F_ONE;

  /* quantize */
  return sample >> (MAD_F_FRACBITS + 1 - 16);
}

/*
 * This is the output callback function. It is called after each frame of
 * MPEG audio data has been completely decoded. The purpose of this callback
 * is to output (or play) the decoded PCM audio.
 */

static
enum mad_flow output(void *data,
		     struct mad_header const *header,
		     struct mad_pcm *pcm)
{
  unsigned int nchannels, nsamples;
  mad_fixed_t const *left_ch, *right_ch;

  int16_t * lout,*rout;

  /* pcm->samplerate contains the sampling frequency */

  nchannels = pcm->channels;
  nsamples  = pcm->length;
  left_ch   = pcm->samples[0];  //左边通道数据指针
  right_ch  = pcm->samples[1];  //右边通道数据指针

  lout = (int16_t *)pcm->samples[0];
  rout = (int16_t *)pcm->samples[1];

  while (nsamples--)
  {
    *lout++ = scale(*left_ch++);

    if (nchannels == 2) {
        *rout++ = scale(*right_ch++);
    }
  }
  //双通道变单通道
  if(nchannels == 2)
  {
    lout = (int16_t *)pcm->samples[0];
    rout = (int16_t *)pcm->samples[1];
    nsamples  = pcm->length;
    while (nsamples--)
    {
        int16_t newSample = ((signed int)*lout + (signed int)*rout)/2;
        *lout = newSample;
        lout++;
        rout++;
    }
  }
  //写入数据到输出
  fwrite(pcm->samples[0],pcm->length,2,ppcmFile);
  return MAD_FLOW_CONTINUE;
}

/*
 * This is the error callback function. It is called whenever a decoding
 * error occurs. The error is indicated by stream->error; the list of
 * possible MAD_ERROR_* errors can be found in the mad.h (or stream.h)
 * header file.
 */

static
enum mad_flow error(void *data,
		    struct mad_stream *stream,
		    struct mad_frame *frame)
{
  struct buffer *buffer = data;

  __android_log_print(ANDROID_LOG_ERROR,TAG,"decoding error 0x%04x (%s) at byte offset %u\n",
                      stream->error, mad_stream_errorstr(stream),
            stream->this_frame - buffer->start);

  /* return MAD_FLOW_BREAK here to stop decoding (and propagate an error) */

  return MAD_FLOW_CONTINUE;
}

/*
 * This is the function called by main() above to perform all the decoding.
 * It instantiates a decoder object and configures it with the input,
 * output, and error callback functions above. A single call to
 * mad_decoder_run() continues until a callback function returns
 * MAD_FLOW_STOP (to stop decoding) or MAD_FLOW_BREAK (to stop decoding and
 * signal an error).
 */

static
int decode(unsigned char const *start, unsigned long length)
{
  struct buffer buffer;
  struct mad_decoder decoder;
  int result;

  /* initialize our private message structure */

  buffer.start  = start;
  buffer.length = length;

  /* configure input, output, and error functions */

  mad_decoder_init(&decoder, &buffer,
		   input, 0 /* header */, 0 /* filter */, output,
		   error, 0 /* message */);

  /* start decoding */

  result = mad_decoder_run(&decoder, MAD_DECODER_MODE_SYNC);

  /* release the decoder */

  mad_decoder_finish(&decoder);

  return result;
}
