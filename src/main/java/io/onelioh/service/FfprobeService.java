package io.onelioh.service;

import io.onelioh.model.MediaInfo;
import io.onelioh.model.MediaStream;
import io.onelioh.model.MediaType;

import java.io.File;
import java.util.List;

public class FfprobeService {
    private static final String FFPROBE = "ffprobe";

    public static MediaInfo analyze(File file) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    FFPROBE,
                    "-v", "error",
                    "-print_format", "json",
                    "-show_streams",
                    file.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();

            String json = new String(p.getInputStream().readAllBytes());
            int code = p.waitFor();
            if (code != 0) {
                System.err.println("ffprobe exit code " + code);
                return new MediaInfo(List.of(), List.of());
            }

            // Parse JSON
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            var root = mapper.readTree(json);
            var streams = root.get("streams");
            if (streams == null || !streams.isArray()) {
                return new MediaInfo(List.of(), List.of());
            }

            var vids = new java.util.ArrayList<MediaStream>();
            var auds = new java.util.ArrayList<MediaStream>();

            for (var s : streams) {
                String type = s.path("codec_type").asText(); // "video" | "audio" | "subtitle" ...
                String codec = s.path("codec_name").asText();
                String lang  = s.path("tags").path("language").asText("");
                String title = s.path("tags").path("title").asText("");

                if ("video".equals(type)) {
                    MediaStream videoStream = new MediaStream(MediaType.VIDEO, (codec.isEmpty() ? "video" : codec), 1, lang.isEmpty() ? "" : " [" + lang + "]", title.isEmpty() ? "" : " — " + title);
                    vids.add(videoStream);
                } else if ("audio".equals(type)) {
                    MediaStream audioStream = new MediaStream(MediaType.AUDIO, (codec.isEmpty() ? "audio" : codec), 1, lang.isEmpty() ? "" : " [" + lang + "]", title.isEmpty() ? "" : " — " + title);
                    auds.add(audioStream);
                }
            }

            return new MediaInfo(vids, auds);

        } catch (Exception ex) {
            ex.printStackTrace();
            return new MediaInfo(List.of(), List.of());
        }
    }
}
