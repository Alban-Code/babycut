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
            System.out.println("command: " + String.join(" ", pb.command()));
            pb.redirectErrorStream(true);
            Process p = pb.start();

            String json = new String(p.getInputStream().readAllBytes());
            System.out.println("Json: " + json);
            int code = p.waitFor();
            if (code != 0) {
                System.err.println("ffprobe exit code " + code);
                return new MediaInfo();
            }

            // Parse JSON
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            var root = mapper.readTree(json);
            var streams = root.get("streams");
            if (streams == null || !streams.isArray()) {
                return new MediaInfo();
            }

            var vids = new java.util.ArrayList<MediaStream>();
            var auds = new java.util.ArrayList<MediaStream>();
            double durationLol = -1;

            for (var s : streams) {
                String type = s.path("codec_type").asText(); // "video" | "audio" | "subtitle" ...
                String codec = s.path("codec_name").asText();
                String lang  = s.path("tags").path("language").asText("");
                String title = s.path("tags").path("title").asText("");

                if ("video".equals(type)) {
                    MediaStream videoStream = new MediaStream(MediaType.VIDEO, (codec.isEmpty() ? "video" : codec), 1, lang.isEmpty() ? "" : " [" + lang + "]", title.isEmpty() ? "" : " — " + title);
                    vids.add(videoStream);
                    if (durationLol <= 0) {
                        durationLol = s.path("duration").asDouble();
                    }
                } else if ("audio".equals(type)) {
                    System.out.println("Audio" + codec + s.path("index"));
                    MediaStream audioStream = new MediaStream(MediaType.AUDIO, (codec.isEmpty() ? "audio" : codec), 1, lang.isEmpty() ? "" : " [" + lang + "]", title.isEmpty() ? "" : " — " + title);
                    auds.add(audioStream);
                }
            }

            return new MediaInfo(vids, auds, durationLol);

        } catch (Exception ex) {
            ex.printStackTrace();
            return new MediaInfo();
        }
    }
}
