package tsu.tsu_05;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * 使用 Phaser 类同步三个并发任务
 * 分为以下三个步骤：
 * 1. 在指定的文件夹及其子文件夹中获取拓展名为 .log 的文件
 * 2. 对第一步的结果进行过滤，删除修改时间超过 24 小时的文件
 * 3. 将结果打印到控制台
 * <p>
 * 在第一步和第二步的时候，都会检查所查找的结果列表是不是有元素存在，如果列表为空，对应的线程将结束执行，并且从 phaser 中删除
 */
public class Main {

    public static void main(String[] args) {

        // 创建一个 Phaser 对象，包含 3 个参与者
        Phaser phaser = new Phaser(3);

        // 创建 3个 FileSearch 对象，分别搜索不同的文件夹
        FileSearch system = new FileSearch("C:\\Windows", "log", phaser);
        FileSearch apps = new FileSearch("C:\\Program Files", "log", phaser);
        FileSearch documents = new FileSearch("C:\\Documents And Settings", "log", phaser);

        Thread systemThread = new Thread(system, "System");
        systemThread.start();

        Thread appsThread = new Thread(apps, "Apps");
        appsThread.start();

        Thread documentsThread = new Thread(documents, "Documents");
        documentsThread.start();

        try {
            systemThread.join();
            appsThread.join();
            documentsThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Terminated: %s\n", phaser.isTerminated());
    }

}

class FileSearch implements Runnable {

    private final String initPath;
    private final String fileExtension;
    private List<String> results;
    private Phaser phaser;

    public FileSearch(String initPath, String fileExtension, Phaser phaser) {
        this.initPath = initPath;
        this.fileExtension = fileExtension;
        this.phaser = phaser;
        results = new ArrayList<>();
    }

    @Override
    public void run() {

        // 等待创建所有的 FileSearch 对象
        phaser.arriveAndAwaitAdvance();

        System.out.printf("%s: Starting.\n", Thread.currentThread().getName());

        // 第一阶段：查找文件
        File file = new File(initPath);
        if (file.isDirectory()) {
            directoryProcess(file);
        }
        // 如果没结果，移除当前的 phaser，结束
        if (!checkResults()) {
            return;
        }

        // 第二步：筛选结果
        filterResults();
        if (!checkResults()) {
            return;
        }

        // 第三步：输出信息到控制台
        showInfo();
        phaser.arriveAndDeregister();
        System.out.printf("%s: Work completed.\n", Thread.currentThread().getName());

    }

    /**
     * 打印搜索结果到控制台
     */
    private void showInfo() {
        for (int i = 0; i < results.size(); i++) {
            File file = new File(results.get(i));
            System.out.printf("%s: %s\n", Thread.currentThread().getName(), file.getAbsolutePath());
        }

        // 等待所有的搜索线程结束
        phaser.arriveAndAwaitAdvance();
    }

    /**
     * 处理完一个阶段后，用这个方法来判断是否有结果
     * 如果没有，注销当前的线程
     *
     * @return 如果有结果返回 true ，否则返回 false
     */
    private boolean checkResults() {
        if (results.isEmpty()) {
            System.out.printf("%s: Phase %d: 0 results.\n", Thread.currentThread().getName(), phaser.getPhase());
            System.out.printf("%s: Phase %d: End.\n", Thread.currentThread().getName(), phaser.getPhase());
            // 没结果就注销
            phaser.arriveAndDeregister();
            return false;
        } else {
            // 有结果就进入下一阶段
            System.out.printf("%s: Phase %d: %d results.\n", Thread.currentThread().getName(), phaser.getPhase(),
                    results.size());
            phaser.arriveAndAwaitAdvance();
            return true;
        }
    }

    /**
     * 筛选出最后修改时间不超过一天的文件
     */
    private void filterResults() {
        List<String> newResults = new ArrayList<>();
        long actualDate = new Date().getTime();
        for (int i = 0; i < results.size(); i++) {
            File file = new File(results.get(i));
            long fileDate = file.lastModified();

            if (actualDate - fileDate < TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) {
                newResults.add(results.get(i));
            }
        }
        results = newResults;
    }

    /**
     * 递归的处理文件夹
     */
    private void directoryProcess(File file) {

        // 获取文件夹下的所有文件
        File list[] = file.listFiles();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    // 处理子文件夹
                    directoryProcess(list[i]);
                } else {
                    // 处理文件
                    fileProcess(list[i]);
                }
            }
        }
    }

    /**
     * 处理一个文件，将符合条件的结果加入到列表中
     */
    private void fileProcess(File file) {
        if (file.getName().endsWith(fileExtension)) {
            results.add(file.getAbsolutePath());
        }
    }

}