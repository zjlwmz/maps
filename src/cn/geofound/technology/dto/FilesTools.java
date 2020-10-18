package cn.geofound.technology.dto;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
/** 
 * <pre> 
 * 文件操作工具类： 
 *      1.复制单个文件 
 *      2.复制文件夹(包括里边的文件) 
 *      3.删除单个文件 
 *      4.删除文件夹(包括里边的文件) 
 *      5.重名单个文件 
 *      6.重命名文件夹中相同类型的文件格式：XXX_1.jpg、XXX_2.jpg、XXX_3.jpg 
 *      7.剪切单个文件到指定文件夹 
 *      8.剪切文件夹到指定文件夹(包含里边的文件) 
 *      9.指定文件夹下指定类型文件统计数量(包含里边文件夹下的符合条件文件)，并返回每个符合条件的文件的绝对路径字符串ArrayList 
 *      10.指定目录下查找符合条件的文件，并返回包含符合条件的文件绝对路径字符串ArrayList 
 * </pre> 
 * <pre> 
 * @author kaifang 
 * @version 1.0 
 * </pre> 
 */  
  
public class FilesTools {  
    // 创建ArrayList对象保存数量及查找到的路径  
    private static ArrayList<String> list = null;  
    // 创建intb变量保存查找数量  
    private static int sum = 0;  
  
    public FilesTools() {  
        super();  
        list = new ArrayList<String>();  
        list.add(0, "-1");  
    }  
  
    /** 
     * 1.复制单个文件 <br> 
     * 2.复制文件夹(包括里边的文件) 
     *  
     * @param sorFile 
     *            需要复制的文件或文件夹绝对路径 
     * @param toFile 
     *            目标文件夹绝对路径 
     * @return <br> 
     *         boolean true 复制成功 false 复制失败 
     */  
    public static boolean copyFils(String sorFile, String toFile) throws IOException {  
        // 定义返回值变量  
        boolean flag = false;  
        // 封装字符路径为File对象  
        File sorfile = new File(sorFile);  
        File tofile = new File(toFile);  
        // 声明字节流  
        FileInputStream fileInputStream = null;  
        FileOutputStream fileOutputStream = null;  
        if (sorfile.isFile()) {  
            // 创建FileInputStream字节流读取文件对象  
            fileInputStream = new FileInputStream(sorfile);  
            // 创建FileOutputStream字节流写入文件对象  
            File temp = new File(tofile.getAbsolutePath()  
                    .concat(File.separator).concat(sorfile.getName()));  
            fileOutputStream = new FileOutputStream(temp);  
            // 定义字节数组变量，每次都读进字节数组  
            byte[] by = new byte[1024 * 1000 * 2];  
            // 定义每次真实读取到的字节长度变量  
            int len = 0;  
            // 循环复制文件  
            while ((len = fileInputStream.read(by)) != -1) {  
                fileOutputStream.write(by, 0, len);  
            }  
            // 关闭流  
            fileInputStream.close();  
            fileOutputStream.close();  
            // 如果源文件长度等于复制后的文件长度，则设置返回变量 true  
            return true;  
        } else {  
            // 获取源文件夹下的所有文件包含文件夹的File列表  
            File[] files = sorfile.listFiles();  
            // 判断是否获取到的List对象为null,null说明是特殊文件或没权限访问  
            if (files != null) {  
                // 拼接新的文件夹路径并创建  
                File temp = new File(tofile.getAbsolutePath()  
                        .concat(File.separator).concat(sorfile.getName()));  
                temp.mkdir();  
                // 循环调用方法本身  
                for (File file : files) {  
                    flag = copyFils(file.getAbsolutePath(),  
                            temp.getAbsolutePath());  
                }  
            } else {  
                System.out.println("文件夹下有特殊文件夹！可能是隐藏的垃圾桶文件。 "  
                        + sorfile.getAbsolutePath());  
            }  
        }  
        return flag;  
    }  
  
    /** 
     * 3.删除单个文件 <br> 
     * 4.删除文件夹(包括里边的文件) 
     *  
     * @param delFile 
     *            需要删除的文件绝对路径或文件夹绝对路径 
     * @return <br> 
     *         boolean true 删除成功 false 删除失败 
     */  
    public static boolean deleteFiles(String delFile) throws IOException {  
        // 定义返回值标志  
        boolean flag = false;  
        // 封装字符串路径为File对象  
        File delfile = new File(delFile);  
        // 判断是否为文件，若是则直接删除  
        if (delfile.isFile()) {  
            return delfile.delete();  
        } else {  
            // 不是文件，则获取当前文件夹下所有文件组成的File对象  
            File[] files = delfile.listFiles();  
            // 判断是否null,null则是特殊文件夹  
            if (files != null) {  
                // 循环调用方法自己  
                for (File file : files) {  
                    flag = deleteFiles(file.getAbsolutePath());  
                }  
                // 当前文件夹下的文件删完后，删除当前文件夹  
                delfile.delete();  
            } else {  
                System.out.println("文件夹下有特殊文件夹！可能是隐藏的垃圾桶文件，不能删除！ "  
                        + delfile.getAbsolutePath());  
            }  
        }  
        return flag;  
    }  
  
    /** 
     * 5.重名单个文件 
     *  
     * @param oldFile 
     *            需要重命名的文件绝对路径 
     * @param newName 
     *            新的文件名 
     * @return <br> 
     *         boolean true 重命名成功 false 重命名失败 
     */  
    public static boolean renameFile(String oldFile, String newName)  
            throws IOException {  
        // 定义返回值标志  
        boolean flag = false;  
        // 封装字符创路径为File对象  
        File oldfile = new File(oldFile);  
        File newfile = new File(oldfile.getParent().concat(File.separator)  
                .concat(newName));  
        if (oldfile.isFile()) {  
            flag = oldfile.renameTo(newfile);  
        } else {  
            System.out.println("指定的文件绝对路径有误！ " + oldFile);  
        }  
        return flag;  
    }  
  
    /** 
     * 6.重命名文件夹中相同类型的文件格式：XXX_1.jpg、XXX_2.jpg、XXX_3.jpg 
     *  
     * @param files 
     *            文件所在的文件夹绝对路径 
     * @param type 
     *            指定类型的文件 
     * @param name 
     *            指定文件名的前一部分 
     * @return <br> 
     *         boolean true 重命名成功 false 重命名失败 
     */
    public static boolean renameFile(String files, String type, String name)  
            throws IOException {  
        // 返回值标志  
        boolean flag = false;  
        // 传进来的类型文件名变量转为常量，不然之后的匿名问价过滤内部类不能使用  
        final String typ = type.contains(".") ? type : ".".concat(type);  
        // 封装路径为File对象  
        File file = new File(files);  
        // 获取文件夹下所有符合条件的文件数组  
        File[] fil = file.listFiles(new FileFilter() {  
              
            public boolean accept(File path) {  
                return path.isFile()  
                        && path.getName().toLowerCase()  
                                .endsWith((typ.toLowerCase()));  
            }  
        });  
        int i = 1;  
        for (File fi : fil) {  
            flag = fi.renameTo(new File(fi.getParent().concat(File.separator)  
                    .concat(name).concat("_")  
                    + (i++) + typ));  
        }  
        return flag;  
    }  
  
    /** 
     * 7.剪切单个文件到指定文件夹 <br> 
     * 8.剪切文件夹到指定文件夹(包含里边的文件) 
     *  
     * @param file 
     *            需要剪切的文件或文件夹所在绝对路径 
     * @param toFiles 
     *            剪切到的文件夹绝对路径 
     * @return <br> 
     *         boolean true 剪切成功 false 剪切失败 
     */  
    public static boolean clipFile(String file, String toFiles) throws IOException {  
        boolean flag = false;  
        // 封装目录  
        File sfile = new File(file);  
        File tofile = new File(toFiles);  
        // 判断需要剪切的是否单个文件  
        if (sfile.isFile()) {  
            flag = sfile.renameTo(new File(toFiles.concat(File.separator)  
                    .concat(sfile.getName())));  
        } else {  
            // 调用本类中的复制文件方法  
            flag = copyFils(sfile.getAbsolutePath(), tofile.getAbsolutePath());  
            if (flag) {  
                // 文件夹复制完成后，删除原文件夹  
                flag = deleteFiles(sfile.getAbsolutePath());  
            }  
        }  
        return flag;  
    }  
  
    /** 
     * 9.指定文件夹下指定类型文件统计数量(包含里边文件夹下的符合条件文件)，并返回每个符合条件的文件的绝对路径字符串ArrayList 
     *  
     * @param findFiles 
     *            查找的目录绝对路径 
     * @param type 
     *            查找的文件类型 格式：.jpg、.exe(不区分大小写) 
     * @return <br> 
     *         ArrayList<String> 返回所有符合条件的文件绝对路径的字符串ArrayList<br> 
     *         注意：返回值ArrayList<String>对象的xX.get(0)是统计的数量，即就是第一个存储的是统计结果 
     */ 
    public static ArrayList<String> findFileType(String findFiles, String type)  
            throws IOException {  
        // 类型type字符串转为常量，便于匿名内部类使用  
        final String typ = type.contains(".") ? type : ".".concat(type);  
        // 封装查找的目录为File对象  
        File findfiles = new File(findFiles);  
        // 获取当前目录下File数组  
        File[] findfile = findfiles.listFiles();  
        if (findfiles != null) {  
            for (File find : findfile) {  
                if (find.isDirectory()) {  
                    findFileType(find.getAbsolutePath(), typ);  
                } else {  
                    if (find.getName().toLowerCase()  
                            .endsWith(typ.toLowerCase())) {  
                        list.add(find.getAbsolutePath());  
                        sum++;  
                    }  
                }  
            }  
        }  
        list.set(0, String.valueOf(sum));  
        return list;  
    }  
  
    /** 
     * 10.指定目录下查找符合条件的文件，并返回包含符合条件的文件绝对路径字符串ArrayList 
     *  
     * @param findFiles 
     *            查找的目录绝对路径 
     * @param file 
     *            查找的文件名，可以是一部分也可以全名 
     * @return <br> 
     *         ArrayList<String> <br> 
     *         返回所有符合条件的文件绝对路径的字符串ArrayList 
     */  
    public static ArrayList<String> findFile(String findFiles, String file)  
            throws IOException {  
        // 封装路径  
        File findfiles = new File(findFiles);  
        // 判断是否查找指定类型的  
        if ("*.".equals(file.substring(0, 2))) {  
            // 调用上边查找指定类型的方法  
            findFileType(findFiles, file.substring(1));  
        } else {  
            // 调用分离出去的方法  
            getFile(findfiles, file);  
        }  
        // 移除索引是0的元素  
        list.remove(0);  
        return list;  
    }  
  
    // findFile(String findFiles, String file)方法，当查找的是包含某个字符串是，分离便于递归调用<br>  
    private static void getFile(File findFiles, String file) {  
        File[] files = findFiles.listFiles();  
        if (files != null) {  
            for (File f : files) {  
                if (f.isFile()  
                        && f.getName().toLowerCase()  
                                .contains(file.toLowerCase())) {  
                    list.add(f.getAbsolutePath());  
                } else {  
                    if (f.getName().toLowerCase().contains(file.toLowerCase())) {  
                        list.add(f.getAbsolutePath());  
                    }  
                    getFile(f, file);  
                }  
            }  
        }  
    }  
}  