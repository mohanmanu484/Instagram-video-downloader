package com.mohang.instadownload.instadownloader.instadownloader;

public class Media {

    Graphql graphql;

    public Graphql getGrapgql() {
        return graphql;
    }

    public class Graphql {

        ShortCodeMedia shortcode_media;

        public ShortCodeMedia getShortcode_media() {
            return shortcode_media;
        }
    }

   public class ShortCodeMedia {

        boolean is_video;
        String display_url;
        String video_url;

        public boolean isIs_video() {
            return is_video;
        }

        public String getDisplay_url() {
            return display_url;
        }

        public String getVideo_url() {
            return video_url;
        }
    }
}
