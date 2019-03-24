package com.example.boomq.musicplayer.LrcView;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boomq on 2019/3/24.
 */

public class LrcRow implements Comparable<LrcRow>{
    public final static String TAG = "LrcRow";
    public String strTime;
    public long time;
    public String content;

    public LrcRow(){}

    public LrcRow(String strTime,long time,String content){
        this.strTime = strTime;
        this.time = time;
        this.content = content;
    }

    @Override
    public String toString() {
        return "[" + strTime + " ]"  + content;
    }

    /**
     * 读取歌词的每一行内容，转换为LrcRow，加入到集合中
     */
    public static List<LrcRow> createRows(String standardLrcLine){

        try{//判断是否包含为有时间的歌词内容（两种歌词文件都进行判断）
            if (standardLrcLine.indexOf("[") == 0){
                if (standardLrcLine.indexOf("]") == 6 ||standardLrcLine.indexOf("]") == 9 ){

                }else{
                    if (standardLrcLine.indexOf(".") == 6){

                    }else {
                        return null;
                    }
                }
            }else{
                return null ;
            }

            //[02:34.14][01:07.00]当你我不小心又想起她
            //找到最后一个 ‘]’ 的位置
            int lastIndexOfRightBracket = standardLrcLine.lastIndexOf("]");
            //歌词内容就是 ‘]’ 的位置之后的文本   eg:   当你我不小心又想起她
            String content = standardLrcLine.substring(lastIndexOfRightBracket + 1, standardLrcLine.length());
            //歌词时间就是 ‘]’ 的位置之前的文本   eg:   [02:34.14][01:07.00]

            /**
             将时间格式转换一下  [mm:ss.SS][mm:ss.SS] 转换为  -mm:ss.SS--mm:ss.SS-
             即：[02:34.14][01:07.00]  转换为      -02:34.14--01:07.00-
             */
            String times = standardLrcLine.substring(0,lastIndexOfRightBracket + 1).replace("[", "-").replace("]", "-");
            //通过 ‘-’ 来拆分字符串
            String arrTimes[] = times.split("-");
            List<LrcRow> listTimes = new ArrayList<LrcRow>();
            for(String temp : arrTimes){
                if(temp.trim().length() == 0){
                    continue;
                }
                /** [02:34.14][01:07.00]当你我不小心又想起她
                 *
                 上面的歌词的就可以拆分为下面两句歌词了
                 [02:34.14]当你我不小心又想起她
                 [01:07.00]当你我不小心又想起她
                 */
                LrcRow lrcRow = new LrcRow(temp, timeConvert(temp), content);
                listTimes.add(lrcRow);
            }
            return listTimes;
        }catch(Exception e){
            Log.e(TAG,"createRows exception:" + e.getMessage());
            return null;
        }
    }

    /**
     * 将解析得到的表示时间的字符转化为Long型
     */
    private static long timeConvert(String timeString){
        if(timeString.length() == 5){
            timeString =timeString+":00";
        }//如果为第一种歌词文件，则将其转换为第二种。
        //因为给如的字符串的时间格式为XX:XX.XX,返回的long要求是以毫秒为单位
        //将字符串 XX:XX.XX 转换为 XX:XX:XX
        timeString = timeString.replace('.', ':');
        //将字符串 XX:XX:XX 拆分
        String[] times = timeString.split(":");
        // mm:ss:SS
        return Integer.valueOf(times[0]) * 60 * 1000 +//分
                Integer.valueOf(times[1]) * 1000 +//秒
                Integer.valueOf(times[2]) ;//毫秒
    }

    /**
     * 排序的时候，根据歌词的时间来排序
     */
    public int compareTo(LrcRow another) {
        return (int)(this.time - another.time);
    }
}

