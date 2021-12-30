package com.faceunity.core.media.video.encoder;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaAudioFileEncoder extends MediaEncoder {
    String TAG = "Video_MediaAudioFileEncoder";
    private static final boolean DEBUG = false;
    private static final String AUDIO = "audio/";

    private MediaExtractor mMediaExtractor;
    private String mFilepath;
    private ByteBuffer mInputBuffer;

    public MediaAudioFileEncoder(final MediaMuxerWrapper muxer, final MediaEncoderListener listener, String filepath) {
        super(muxer, listener);
        mFilepath = filepath;
    }

    @Override
    protected void prepare() throws IOException {
        if (DEBUG) {
            Log.v(TAG, "prepare:");
        }
        mTrackIndex = -1;
        mMuxerStarted = mIsEOS = false;
        mMediaExtractor = new MediaExtractor();
        mMediaExtractor.setDataSource(mFilepath);

        MediaMuxerWrapper muxer = mWeakMuxer.get();
        //分离出音轨和视轨
        int trackCount = mMediaExtractor.getTrackCount();
        if (DEBUG) {
            Log.d(TAG, "getTrackCount: " + trackCount);
        }
        for (int i = 0; i < trackCount; i++) {
            MediaFormat format = mMediaExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith(AUDIO)) {
                int maxInputSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
                mInputBuffer = ByteBuffer.allocate(maxInputSize);
                mMediaExtractor.selectTrack(i);
                mTrackIndex = muxer.addTrack(format);
                break;
            }
        }
        if (mListener != null) {
            try {
                mListener.onPrepared(this);
            } catch (final Exception e) {
                Log.e(TAG, "prepare:", e);
            }
        }
    }

    @Override
    public void run() {
        synchronized (mSync) {
            mSync.notify();
        }
        MediaMuxerWrapper muxer = mWeakMuxer.get();
        if (!muxer.start()) {
            // we should wait until muxer is ready
            synchronized (muxer) {
                while (!muxer.isStarted()) {
                    try {
                        muxer.wait(100);
                    } catch (InterruptedException e) {
                        // ignored
                    }
                }
            }
        }
        if (mTrackIndex < 0) {
            release();
            return;
        }
        mMuxerStarted = true;
        boolean first = false;
        long startWhen = 0;
        while (true) {
            if (mRequestStop) {
                release();
                break;
            }
            int sampleSize = mMediaExtractor.readSampleData(mInputBuffer, 0);
            long timeStamp = mMediaExtractor.getSampleTime();
            int flags = mMediaExtractor.getSampleFlags();
            if (mMediaExtractor.advance() && sampleSize > 0) {
                if (!first) {
                    startWhen = System.currentTimeMillis();
                    first = true;
                }
                try {
                    long sleepTime = (timeStamp / 1000) - (System.currentTimeMillis() - startWhen);
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (InterruptedException e) {
                    // ignored
                }
                mBufferInfo.set(0, sampleSize, getPTSUs(), flags);
                muxer.writeSampleData(mTrackIndex, mInputBuffer, mBufferInfo);
                prevOutputPTSUs = mBufferInfo.presentationTimeUs;
            } else {
                release();
                break;
            }
        }
    }

    @Override
    protected void release() {
        if (DEBUG) {
            Log.d(TAG, "release: ");
        }
        super.release();
        if (mMediaExtractor != null) {
            mMediaExtractor.release();
            mMediaExtractor = null;
        }
    }

    @Override
    protected void drain() {
    }

    @Override
    protected void signalEndOfInputStream() {
    }
}
