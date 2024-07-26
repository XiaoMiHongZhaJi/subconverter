package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

class AddCloud {
    public static void main(String[] args) throws Exception {

        BufferedReader yaml = new BufferedReader(new FileReader("D:\\Documents\\IdeaProjects\\subconverter\\base\\rules\\ACL4SSR\\Clash\\Cloud.yml"));
        BufferedReader cloud = new BufferedReader(new FileReader("D:\\Documents\\IdeaProjects\\subconverter\\base\\rules\\ACL4SSR\\Clash\\Cloud.list"));

        Set<String> yamlServerList = new HashSet<>();
        String line = yaml.readLine();
        while (line != null) {
            int serverIndex = line.indexOf("server:");
            if (serverIndex > -1 && line.indexOf("nameserver") == -1) {
                line = line.substring(serverIndex + 7);
                line = line.substring(0, line.indexOf(",")).trim();
                if("8.8.8.8".equals(line)){
                    line = yaml.readLine();
                    continue;
                }
                String[] split = line.split("\\.");
                if (split.length == 4 && isNumeric(split[0])) {
                    // ip
                    line = line.substring(0, line.lastIndexOf("."));
                }else {
                    // host
                    line = split[split.length - 2] + "." + split[split.length - 1];
                }
                yamlServerList.add(line.trim());
            }
            line = yaml.readLine();
            if ("proxy-groups:".equals(line)) {
                break;
            }
        }
        System.out.println(yamlServerList);

        Set<String> cloudServerList = new HashSet<>();
        line = cloud.readLine();
        while (line != null) {
            if (line.startsWith("#") || line.indexOf(",") == -1) {
                line = cloud.readLine();
                continue;
            }
            String[] split = line.split(",");
            if (split.length >= 2) {
                line = split[1].trim();
                split = line.split("\\.");
                if (split.length == 4 && isNumeric(split[0])) {
                    // ip
                    line = line.substring(0, line.lastIndexOf("."));
                }else {
                    // host
                    line = split[split.length - 2] + "." + split[split.length - 1];
                }
                cloudServerList.add(line);
            }
            line = cloud.readLine();
        }
        System.out.println(cloudServerList);

        // 待增加的地址
        System.out.println("待增加的地址列表：");
        for (String server : yamlServerList) {
            if(!cloudServerList.contains(server)){
                String[] split = server.split("\\.");
                if(split.length == 3 && isNumeric(split[0])){
                    // ip
                    System.out.println("IP-CIDR," + server + ".0/24,no-resolve");
                }else {
                    // host
                    System.out.println("DOMAIN-SUFFIX," + server);
                }
            }
        }

        // 完整的地址
        System.out.println("\n\n完整的地址列表：");
        yamlServerList.addAll(cloudServerList);
        List<String> list = new ArrayList<>(yamlServerList);
        Collections.sort(list, (server1, server2) -> {
            String[] split1 = server1.split("\\.");
            String[] split2 = server2.split("\\.");
            if(split1.length == 3 && isNumeric(split1[0])){
                // ip
                if(split2.length == 3 && isNumeric(split2[0])){
                    if(Integer.parseInt(split1[0]) == Integer.parseInt(split2[0])){
                        return Integer.parseInt(split1[1]) - Integer.parseInt(split2[1]);
                    }
                    return Integer.parseInt(split1[0]) - Integer.parseInt(split2[0]);
                }
                return 1;
            }
            if(split2.length == 3 && isNumeric(split2[0])){
                return -1;
            }
            return split1[0].compareTo(split2[0]);
        });
        for (String server : list) {
            String[] split = server.split("\\.");
            if(split.length == 3 && isNumeric(split[0])){
                // ip
                System.out.println("IP-CIDR," + server + ".0/24,no-resolve");
            }else {
                // host
                System.out.println("DOMAIN-SUFFIX," + server);
            }
        }
    }

    public static boolean isNumeric(final CharSequence cs) {
        // 判断是否为空，如果为空则返回false
        if (cs == null) {
            return false;
        }
        // 通过 length() 方法计算cs传入进来的字符串的长度，并将字符串长度存放到sz中
        final int sz = cs.length();
        // 通过字符串长度循环
        for (int i = 0; i < sz; i++) {
            // 判断每一个字符是否为数字，如果其中有一个字符不满足，则返回false
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        // 验证全部通过则返回true
        return true;
    }
}