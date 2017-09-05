package WebSpider;

import org.apache.commons.lang3.RandomStringUtils;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.List;

/**
 * Description:爬取zhihu.com上指定问题的图片
 * <p>
 * Created by lhan On 2017/9/4 10:06
 */

// 程序会产生MalformedURLException，但不影响程序运行结果，个人分析为网络数据包产生的异常
public class GetquestionUrlProcessor implements PageProcessor
{
    // 设置编码 ，超时时间，重试次数，
    private Site site = Site.me().setRetryTimes(10).setSleepTime(5000).setTimeOut(5000)
            .addCookie("Domain", "zhihu.com")
            // 这里网页上很多cokkie可以随便写一个，注意key value的对应
            .addCookie("z_c0", "Mi4xdEhDWUF3QUFBQUFBRU1KTm9JRVhEQmNBQUFCaEFsVk55Y2pFV1FDQnZGWW1sV2hteUlwX1FqT1pZa3dzVHJLdUJn|1503476681|a84bd8d945834827b82ebc77c65957ab4a793a49")
            .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
    //问题的索引
    //https://www.zhihu.com/question/20902967
    private static final String URL_question = "^https://www\\.zhihu\\.com/question/\\d+$";
    //https://www.zhihu.com/question/19647535/answer/110944270
    private static final String URL_answer = "https://www\\.zhihu\\.com/question/\\d+/answer/\\d+";

    private static String questionId = "";

    @Override
    public void process(us.codecraft.webmagic.Page page)
    {
        //页面为问题页，则将答案链接循环加入Downloader
        if (page.getUrl().regex(URL_question).match())
        {
            int total = 20;
            int time = total / 20;
            page.setCharset("UTF-8");
            for (int i = 0; i <= time; i++)
            {
                int offset = i * 20;
                int limit = total < (i + 1) * 20 ? total : ((i + 1) * 20 - 1);
                String url = "https://www.zhihu.com/api/v4/questions/" + questionId + "/answers?include=data%5B*%5D.is_normal%2Cis_sticky%2Ccollapsed_by%2Csuggest_edit%2Ccomment_count%2Ccan_comment%2Ccontent%2Ceditable_content%2Cvoteup_count%2Creshipment_settings%2Ccomment_permission%2Cmark_infos%2Ccreated_time%2Cupdated_time%2Crelationship.is_authorized%2Cis_author%2Cvoting%2Cis_thanked%2Cis_nothelp%2Cupvoted_followees%3Bdata%5B*%5D.author.badge%5B%3F(type%3Dbest_answerer)%5D.topics&offset=" + offset + "&limit=" + limit + "&sort_by=default";
                page.addTargetRequest(url);
                page.getRequest().setCharset("UTF-8");
            }
            //某个具体答案详情页面，则获取详情信息 。
        }
        else if (page.getUrl().regex(URL_answer).match())
        {
            String questionTitle = page.getHtml().xpath("//h1[@class=QuestionHeader-title]/text()").toString();
            String user_Avatar = page.getHtml().xpath("//span[@class=AuthorInfo-avatarWrapper]//img/@src").toString();
            List<String> urlList = page.getHtml().xpath("//div[@class=RichContent-inner]//img/@src").all();
            String filePath = "E:\\PhotoInZhihu";
            String title = questionTitle + "_" + user_Avatar;
            final int[] i = {1};
            urlList.forEach(url -> {
                try
                {
                    url = URLDecoder.decode(url, "UTf-8");
                    downloadPicture(url, filePath, RandomStringUtils.randomNumeric(5)
                            + url.substring(url.lastIndexOf(".")));
                    i[0]++;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            });
        }
        else
        {
            List<String> id = new JsonPathSelector("$.data[*].id").selectList(page.getRawText());
            for (int i = 0; i < id.size(); i++)
            {
                String answerUrl = "https://www.zhihu.com/question/" + questionId + "/answer/" + id.get(i);
                page.addTargetRequest(answerUrl);
            }
        }
    }

    @Override
    public Site getSite()
    {
        return site;
    }

    public static void downloadPicture(String urlString, String savePath, String filename) throws Exception
    {
        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        //设置请求超时为5s
        con.setConnectTimeout(5 * 1000);
        // 输入流
        InputStream is = con.getInputStream();
        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        File sf = new File(savePath + "/" + filename);
//		if(!sf.exists()){ // 文件夹不存在即创建
//			sf.mkdirs();
//		}
        OutputStream os = new FileOutputStream(sf);
        // 开始读取
        while ((len = is.read(bs)) != -1)
        {
            os.write(bs, 0, len);
        }
        // 完毕，关闭所有链接
        os.close();
        is.close();
    }

    // main方法做为入口
    public static void main(String[] args)
    {
        questionId = "37787176";
        Site site = new Site();
        site.setCharset("UTF-8");
        Spider.create(new GetquestionUrlProcessor())
                .addUrl("https://www.zhihu.com/question/" + questionId)
                //				.addPipeline(new GetquestionUrlPipeline())
                .thread(20)
                .run();
    }
}