package com.aliax.qlky.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandUtils {
    // 执行外部命令
    public static void executeCommand(String command) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", command);  // Linux命令，Windows可以用 "cmd", "/c" 或直接使用 ProcessBuilder
        Process process = processBuilder.start();

        // 打印命令行输出
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        // 等待命令执行完成
        try {
            int exitCode = process.waitFor();
            System.out.println("Command executed with exit code: " + exitCode);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
