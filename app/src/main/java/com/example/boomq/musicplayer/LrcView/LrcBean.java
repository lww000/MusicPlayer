package com.example.boomq.musicplayer.LrcView;

/**
 * Created by boomq on 2019/3/24.
 */

    public class LrcBean {

        private String lrc;

        private long start;

        private long end;



        public LrcBean() {

        }



        public LrcBean(String text, long start, long end) {

            this.lrc = text;

            this.start = start;

            this.end = end;

        }



        public String getLrc() {

            return lrc;

        }



        public void setLrc(String lrc) {

            this.lrc = lrc;

        }



        public long getStart() {

            return start;

        }



        public void setStart(long start) {

            this.start = start;

        }



        public long getEnd() {

            return end;

        }



        public void setEnd(long end) {

            this.end = end;

        }

    }

