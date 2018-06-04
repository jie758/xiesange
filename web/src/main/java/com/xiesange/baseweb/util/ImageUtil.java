package com.xiesange.baseweb.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.xiesange.core.util.LogUtil;
import com.xiesange.core.util.NullUtil;

public class ImageUtil {
	public static final DecimalFormat FORMAT = new java.text.DecimalFormat("#.##");
	public static final Logger logger = LogUtil.getLogger(ImageUtil.class);

	public static void main(String[] args) throws Exception {
		System.out.println(getImageRate(542,543));
		//zoom("C:\\Users\\Think\\Desktop\\banner\\img\\p.png","C:\\Users\\Think\\Desktop\\banner\\img\\p_small.png", 500);
		/*
		 * zoom("C:\\Users\\Think\\Desktop\\image\\22.jpg",
		 * "C:\\Users\\Think\\Desktop\\image\\22_small.jpg", 300);
		 */

		/*
		 * String dir = "C:\\Users\\Think\\Desktop\\topic\\20000104";
		 * 
		 * File fileDir = new File(dir);
		 * 
		 * dealFolder(fileDir);
		 */
	}
	private static void dealFolder(File fileDir) throws Exception {
		File[] files = fileDir.listFiles();
		if (NullUtil.isEmpty(files)) {
			return;
		}
		for (File file : files) {
			String path = file.getAbsolutePath();
			if (path.indexOf(".small.") > -1) {
				continue;
			}
			if (file.isDirectory()) {
				dealFolder(file);
			} else {
				String output = null;
				if (path.indexOf(".large.") > -1) {
					output = path.replace(".large.", ".small.");
					zoom(path, output, 450);
				}
			}
		}
	}

	private static void zoomOther(Image img, String outputPath, int newWidth,
			int newHeight) throws IOException {
		BufferedImage bfImage = new BufferedImage(newWidth, newHeight,
				BufferedImage.TYPE_INT_RGB);
		bfImage.getGraphics().drawImage(
				img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH),
				0, 0, null);

		FileOutputStream os = new FileOutputStream(outputPath);

		String formatName = outputPath
				.substring(outputPath.lastIndexOf(".") + 1);
		ImageIO.write(bfImage, formatName, os);

		os.close();
	}

	private static void zoomPNG(Image img, String outputPath, int newWidth,
			int newHeight) throws IOException {

		try {
			BufferedImage to = new BufferedImage(newWidth, newHeight,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = to.createGraphics();

			to = g2d.getDeviceConfiguration().createCompatibleImage(newWidth,
					newHeight, Transparency.TRANSLUCENT);

			g2d.dispose();
			g2d = to.createGraphics();
			Image from = img.getScaledInstance(newWidth, newHeight,
					Image.SCALE_AREA_AVERAGING);
			g2d.drawImage(from, 0, 0, null);
			g2d.dispose();

			String formatName = outputPath.substring(outputPath
					.lastIndexOf(".") + 1);
			ImageIO.write(to, formatName, new File(outputPath));

		} catch (IOException e) {
			logger.error(e, e);
		}
	}

	/**
	 * 缩放图片
	 * 
	 * @param is
	 *            ,原始的图片文件输入流
	 * @param outputPath
	 *            ，缩放后的图片存储路径
	 * @param newWidth
	 *            ,缩放后的图片宽度，高度要根据原图的高度尺寸等比计算出来
	 * @author Wilson
	 * @throws FileNotFoundException
	 * @date 下午3:37:16
	 */
	public static void zoom(InputStream is, String outputPath, int newWidth)
			throws IOException {
		// 获得源文件
		Image img = ImageIO.read(is); // 如果是本地图片处理的话，这个地方直接把file放到ImageIO.read(file)中即可，如果是执行上传图片的话，
		// 判断图片格式是否正确
		if (img.getWidth(null) == -1)
			return;
		// 为等比缩放计算输出的图片宽度及高度
		int orig_w = img.getWidth(null);
		int orig_h = img.getHeight(null);
		if (orig_w < newWidth) {
			return;
		}
		double rate = (double) orig_w / newWidth;
		int newHeight = ((Double) (orig_h / rate)).intValue();

		String formatName = outputPath
				.substring(outputPath.lastIndexOf(".") + 1);

		if(formatName.equalsIgnoreCase("png")) {
			zoomPNG(img, outputPath, newWidth, newHeight);
		} else {
			zoomOther(img, outputPath, newWidth, newHeight);
		}
	}

	/**
	 * 缩放图片
	 * 
	 * @param origPath
	 *            ,原始的图片路径
	 * @param outputPath
	 *            ，缩放后的图片存储路径
	 * @param finalWidth
	 *            ,缩放后的图片宽度，高度要根据原图的高度尺寸等比调整
	 * @author Wilson
	 * @throws FileNotFoundException
	 * @date 下午3:37:16
	 */
	public static void zoom(String origPath, String outputPath, int finalWidth)
			throws Exception {
		InputStream is = new FileInputStream(origPath);
		zoom(is, outputPath, finalWidth);
		is.close();
	}
	
	/**
	 * 获取某个图片尺寸，返回数组，[width,height]
	 * @param is
	 * @return
	 * @author Wilson 
	 * @throws IOException 
	 * @date 下午2:42:44
	 */
	public static int[] getImageSize(InputStream is) throws IOException{
		Image img = ImageIO.read(is);
		return new int[]{img.getWidth(null),img.getHeight(null)};
	}
	public static int[] getImageSize(File file) throws IOException{
		Image img = ImageIO.read(file);
		return new int[]{img.getWidth(null),img.getHeight(null)};
	}
	public static int[] getImageSize(String filePath) throws IOException{
		Image img = ImageIO.read(new File(filePath));
		return new int[]{img.getWidth(null),img.getHeight(null)};
	}
	/**
	 * 返回图片宽高比
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 * @author Wilson 
	 * @date 下午3:10:16
	 */
	public static float getImageRate(int width,int height) throws IOException{
		return Float.valueOf(FORMAT.format((double)width/height));
	}
}
