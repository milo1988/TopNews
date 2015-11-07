package com.topnews.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class FileUtil {


	public static File writeFile(String fileName) {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		File file = new File(fileName);
		// 如果当前父级文件夹没有创建的话，则创建好父级文件夹
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		try {
			if (!file.exists()) {
				if (!file.createNewFile()) {
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("writeFile FileNotFoundException " + e.toString());
		} catch (IOException e) {
			System.out.println("writeFile IOException " + e.toString());
		} finally {
			try {
				if (osw != null) {
					osw.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 删除文件
	 * 
	 * @param filePathAndName
	 *            String 文件路径及名称 如c:/fqf.txt
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			myDelFile.delete();

		} catch (Exception e) {
			System.out.println("删除文件操作出错" + e.getMessage());
			e.printStackTrace();

		}

	}

	/**
	 * 删除文件夹
	 * 
	 * @param filePathAndName
	 *            String 文件夹路径及名称 如c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹

		} catch (Exception e) {

		}

	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path
	 *            String 文件夹路径 如 c:/fqf
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
	}

	/**
	 * 新建目录
	 * 
	 * @param folderPath
	 *            String 如 c:/fqf
	 * @return boolean
	 */
	public static void newFolder(String folderPath) {
		try {
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.mkdirs();
			if (!myFilePath.exists()) {
				myFilePath.mkdirs();
			}
		} catch (Exception e) {
			System.out.println("新建目录操作出错" + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void write(String path, String content) {
		String encoding = "utf-8";
		File file = new File(path);
		file.delete();
		// 如果当前父级文件夹没有创建的话，则创建好父级文件夹
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String read(String path) {
		String content = "";
		String encoding = "utf-8";
		File file = new File(path);
		if(!file.exists()){
			Log.i("FileUtil", file.getAbsolutePath() + " not exist !!!");
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				content += line + "\n";
			}

			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return content;
	}

	public static void addContent(String file, String conent) {
		BufferedWriter out = null;
		File file1 = new File(file);
		// 如果当前父级文件夹没有创建的话，则创建好父级文件夹
		if (!file1.getParentFile().exists()) {
			file1.getParentFile().mkdirs();
		}
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
			out.write(conent);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static RectF getRectFromFilePath(String filepath) {
		int left = 0;
		int top = 0;
		int right = 0;
		int bottom = 0;
		RectF rect = null;

		int startIndex = filepath.indexOf("(");
		int lastIndex = filepath.indexOf(")");
		if (startIndex != -1 && lastIndex != -1) {
			String[] tempStr = filepath.substring(startIndex + 1, lastIndex).split("-");
			if (tempStr.length == 2) {
				String[] tempStr1 = tempStr[0].split(",");
				String[] tempStr2 = tempStr[1].split(",");
				if (tempStr1.length == 2) {
					left = Integer.parseInt(tempStr1[0].trim());
					top = Integer.parseInt(tempStr1[1].trim());
				}
				if (tempStr2.length == 2) {
					right = Integer.parseInt(tempStr2[0].trim());
					bottom = Integer.parseInt(tempStr2[1].trim());
				}

			}
			rect = new RectF(left, top, right, bottom);
		}

		return rect;
	}

	public static boolean saveBitmap(Bitmap bm, String filePath) {
		boolean saveResut = true;
		File f = new File(filePath);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			saveResut = false;
			e.printStackTrace();
			// System.out.println("FileNotFoundException 异常处理：" +
			// e.getStackTrace());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			saveResut = false;
			e.printStackTrace();
			// System.out.println("IOException 异常处理：" + e.getStackTrace());
		}
		// System.out.println("保存成功！ " + filePath);
		return saveResut;
	}


	/**
	 * 获取手机内部剩余存储空间
	 * 
	 * @return
	 */
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

}
