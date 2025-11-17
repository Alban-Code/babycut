package io.onelioh.babycut.service;

import io.onelioh.babycut.model.media.MediaInfo;
import io.onelioh.babycut.model.media.MediaStream;
import io.onelioh.babycut.model.media.MediaType;

import java.io.File;

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


            for (var s : streams) {
                String type = s.path("codec_type").asText(); // "video" | "audio" | "subtitle" ...
                String codec = s.path("codec_name").asText();
                String lang  = s.path("tags").path("language").asText("");
                String title = s.path("tags").path("title").asText("");
                double durationStream = s.path("duration").asDouble();

                if ("video".equals(type)) {
                    MediaStream videoStream = new MediaStream(MediaType.VIDEO, (codec.isEmpty() ? "video" : codec), 1, lang.isEmpty() ? "" : " [" + lang + "]", title.isEmpty() ? "" : " — " + title, durationStream);
                    vids.add(videoStream);
                } else if ("audio".equals(type)) {
                    System.out.println("Audio" + codec + s.path("index"));
                    MediaStream audioStream = new MediaStream(MediaType.AUDIO, (codec.isEmpty() ? "audio" : codec), 1, lang.isEmpty() ? "" : " [" + lang + "]", title.isEmpty() ? "" : " — " + title, durationStream);
                    auds.add(audioStream);
                }
            }

            // durée de la vidéo en tant que durée du media
            return new MediaInfo(vids, auds, vids.getFirst().getDuration());

        } catch (Exception ex) {
            ex.printStackTrace();
            return new MediaInfo();
        }
    }
}
