import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FetchPicture {
	// 相册地址
	static String url = "http://www.douban.com/photos/album/73791172/";
	// 相册页数
	static int totalpage = 25;
	// 每页多少个图片
	static int everypagecount = 18;

	public static void main(String[] args) throws Exception {
		HttpGet httpget = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(httpget);
		String html = EntityUtils.toString(response.getEntity(), "UTF-8");
		// 获取相册页面

		int addup = 1;
		for (int i = 0; i < totalpage - 1; i++) {
			String page = url + "/?start=" + (i * everypagecount);
			httpget = new HttpGet(page);
			response = client.execute(httpget); // 必须是同一个HttpClient！
			html = EntityUtils.toString(response.getEntity(), "UTF-8");
			analysis(html);
			System.out.println("第 " + addup++ + " 页");
		}
		httpget.releaseConnection();
	}

	public static void analysis(String html) {
		Document doc = Jsoup.parse(html);
		Elements photo_wraps = doc.getElementsByClass("photo_wrap");
		for (Element e : photo_wraps) {
			String imgurl = e.getElementsByTag("img").attr("src");
			String[] names = imgurl.split("/");
			String name = names[names.length - 1];
			String finish = null;
			try {
				finish = download(imgurl, "download/" + name);

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				System.out.println("finish download :" + finish);
			}
		}
	}

	public static String download(String urlString, String filename)
			throws Exception {
		URL url = new URL(urlString);
		URLConnection con = url.openConnection();
		InputStream is = con.getInputStream();

		byte[] bs = new byte[1024];
		int len;
		OutputStream os = new FileOutputStream(filename);
		while ((len = is.read(bs)) != -1) {
			os.write(bs, 0, len);
		}
		os.close();
		is.close();
		return filename;

	}

}
