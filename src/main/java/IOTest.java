import java.io.*;

/**
 * Description: This is lhan's Java IO test.
 * <p>
 * Created by linhan on 2017/11/21 14:01
 */
public class IOTest
{
    public static void main(String[] args)
    {
        /**
         * 字符流的写入
         * 下面的代码不能引用this，因为不能在static方法中调用this关键字
         */
        String path = "D:" + File.separator + "demo.txt";
        /*// 由于IO操作会产生异常，因此需在try/catch block外定义FileWriter的引用
        FileWriter writer = null;
        try
        {
            // 以path为路径创建一个新的FileWriter对象
            // 如果需要覆盖而不是追加，则使用FileWriter(path)构造方法
            writer = new FileWriter(path, true);
            writer.write("lhan is a good boy\r\n");
            // 强制刷新缓冲区写入磁盘
            writer.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            // 如果前面发生异常，则无法产生writer对象，所以需要做判断防止NPE
            if(writer != null)
            {
                try
                {
                    writer.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }*/
        /**
         * 字符流的读取
         */
        /*FileReader reader = null;
        try
        {
            reader = new FileReader(path);
            int temp;
            while ((temp = reader.read()) != -1) {
                System.out.print((char)temp);
            }
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }*/
        /**
         * 使用缓冲区来进行文本文件的复制（Buffer的使用可以提高IO效率）
         */
        /*String copyPath = "D:" + File.separator + "demoCopy.txt";
        FileReader reader = null;
        FileWriter writer = null;
        try
        {
            reader = new FileReader(path);
            writer = new FileWriter(copyPath);
            int temp;
            while ((temp = reader.read()) != -1)
            {
                writer.write(temp);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (writer != null)
            {
                try
                {
                    writer.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }*/
        /**
         * 使用字节流进行文件的复制（加入缓冲区）
         * 字符流，肯定是用于操作类似文本文件或者带有字符文件的场合比较多，而字节流则是操作那些无法直接获取文本信息的二进制文件，比如图片，mp3，视频文件等，
         * 硬盘上都是以字节存储的，只不过字符流在操作文本上面更方便一点而已
         */
        String imgPath = "D:" + File.separator + "demo.jpg";
        String imgCopyPath = "D:" + File.separator + "demoCopy.jpg";
        FileInputStream i = null;
        FileOutputStream o = null;
        // 缓冲区
        BufferedInputStream bi = null;
        BufferedOutputStream bo = null;
        try
        {
            i = new FileInputStream(imgPath);
            o = new FileOutputStream(imgCopyPath);
            bi = new BufferedInputStream(i);
            bo = new BufferedOutputStream(o);
            // 结果为2^15，2<<15表示左移15位
            byte[] bytes = new byte[2<<15];
            int temp;
            while ((temp = bi.read(bytes)) != -1)
            {
                bo.write(bytes, 0, temp);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            // 关闭缓冲区就是关闭缓冲区中的流对象
            if (bi != null)
            {
                try
                {
                    i.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (bo != null)
            {
                try
                {
                    o.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
