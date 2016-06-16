package org.pktzj.mobilesafe.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pktzj on 2016/6/13.
 */

public class NetTrafficEngine {
    public static List<String> QueryNetTraffic(String uid) {
        List<String> traffic = new ArrayList<String>();
        try {
            File file = new File("/proc/uid_stat/" + uid + "/tcp_rcv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            traffic.add(reader.readLine());
            reader.close();
            file = new File("/proc/uid_stat/" + uid + "/tcp_snd");
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            traffic.add(reader.readLine());
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return traffic;
    }
}
