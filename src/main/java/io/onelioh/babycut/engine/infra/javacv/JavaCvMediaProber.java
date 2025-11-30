package io.onelioh.babycut.engine.infra.javacv;

import io.onelioh.babycut.engine.prober.MediaProber;
import io.onelioh.babycut.model.media.MediaInfo;
import io.onelioh.babycut.model.media.MediaStream;
import io.onelioh.babycut.model.media.MediaType;
import org.bytedeco.ffmpeg.avcodec.AVCodecParameters;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avformat.AVStream;
import org.bytedeco.ffmpeg.avutil.AVDictionaryEntry;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import java.io.File;
import java.util.ArrayList;

import static org.bytedeco.ffmpeg.global.avcodec.avcodec_get_name;
import static org.bytedeco.ffmpeg.global.avutil.*;

public class JavaCvMediaProber implements MediaProber {
    @Override
    public MediaInfo analyze(File file) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file)) {
            grabber.start();

            AVFormatContext ctx = grabber.getFormatContext();

            var vids = new ArrayList<MediaStream>();
            var auds = new ArrayList<MediaStream>();

            int nbStreams = ctx.nb_streams();
            for (int i = 0; i < nbStreams; i++) {
                AVStream st = ctx.streams(i);
                AVCodecParameters codecParams = st.codecpar();

                int codecType = codecParams.codec_type();

                String codecName = avcodec_get_name(codecParams.codec_id()).getString();

                String lang = getStreamMetadata(st, "language");
                String title = getStreamMetadata(st, "title");

                double durationStream = 0.0;
                if (st.duration() != 0) {
                    durationStream = st.duration() * av_q2d(st.time_base());
                } else {
                    durationStream = grabber.getLengthInTime() / 1_000_000.0;
                }

                if (codecType == AVMEDIA_TYPE_VIDEO) {
                    MediaStream videoStream = new MediaStream(
                            MediaType.VIDEO,
                            (codecName == null || codecName.isEmpty()) ? "video" : codecName,
                            1,
                            lang.isEmpty() ? "" : " [" + lang + "]",
                            title.isEmpty() ? "" : " - " + title,
                            durationStream
                    );

                    vids.add(videoStream);
                } else if (codecType == AVMEDIA_TYPE_AUDIO) {
                    MediaStream audioStream = new MediaStream(
                            MediaType.AUDIO,
                            (codecName == null || codecName.isEmpty()) ? "audio" : codecName,
                            1,
                            lang.isEmpty() ? "" : " [" + lang + "]",
                            title.isEmpty() ? "" : " - " + title,
                            durationStream
                    );

                    auds.add(audioStream);
                }
            }
            double mainDuration = vids.isEmpty() ? 0.0 : vids.getFirst().getDuration();
            return new MediaInfo(vids, auds, mainDuration);
        } catch (Exception e) {
            e.printStackTrace();
            return new MediaInfo();
        }
    }

    private String getStreamMetadata(AVStream st, String field) {
        AVDictionaryEntry entry = av_dict_get(st.metadata(), field, null, 0);
        if (entry != null && entry.value() != null) {
            return entry.value().getString();
        }
        return "";
    }
}
