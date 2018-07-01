package org.liuyatao.imagedownloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

@SpringBootApplication
public class ImageDownloaderApplication {

	private static final Logger logger = Logger.getLogger("main");


	public static void main(String[] args) throws IOException {

		SpringApplication.run(ImageDownloaderApplication.class, args);

		Commond commond = new Commond();
		if (args[0]==null||args[1]==null||args[2]==null){
			logger.info("输入格式: java -jar ImageDownloader.jar 期号 开始页 结束页");
		}

		int annNum =Integer.parseInt(args[0]);
		int startPageNum=Integer.parseInt(args[1]);
		int endPageNum =Integer.parseInt(args[2]);

		for (int i=startPageNum;i<=endPageNum;i++){
			String url = commond.getImageUrl(i,annNum);
			logger.info("下载地址"+url+" "+i);
			commond.download(url,new File("./"+annNum+"-"+i+".jpg"));

		}


	}
}
