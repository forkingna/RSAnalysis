package BufferedImage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BufferedImageTest {
		
	public static void main(String [] args) {
		String path = "G:\\rose.jpg";
		String secretPath = "G:\\me.jpg";
		String newPath1 = "G:\\informationHiding.jpg";
		String newPath2 = "G:\\secretPhoto.jpg";
		int[][] R = LBSofR(path, secretPath);
		BufferedImage bimg = updatePic(R, 'R', path);
		createNewPic(bimg, newPath1);
		getSecret(57086, 'R', newPath1, newPath2); //57086为秘密信息的字节数
    }
	
	public static void getSecret(int size, int channelCode, String path, String newPath) {
		try {
			BufferedImage bimg = ImageIO.read(new File(path));
			int width = bimg.getWidth();
            int height = bimg.getHeight();
            byte[] secretData = new byte[size];
            int count = 1;
            int index = 0;
            int temp = 0;
            for(int i=0; i<height; i++)
                for(int j=0; j<width; j++) {
                	int channel = 0;
                	switch(channelCode) {
                		case 'R':
                			channel = (bimg.getRGB(i,j)&0xff0000)>>16; //从R通道读取
                			break;
                		case 'G':
                			channel = (bimg.getRGB(i,j)&0xff00)>>8; //从G通道读取
                			break;
                		case 'B':
                			channel = bimg.getRGB(i,j)&0xff; //从B通道读取
                			break;
                		default:
                			break;
                	}
                    temp += (channel&0x1)*Math.pow(2, (count-1)%8); //从低比特位往高比特位补充
                    if((count++)%8 == 0) { //读取到一字节数据
                    	secretData[index++] = (byte)temp;
                    	temp = 0;
                    }
                    if(index == size) {
                    	try {
                    		FileOutputStream fs = new FileOutputStream(newPath);
                    		fs.write(secretData); //生成秘密信息图片
                    		fs.close();
                    		return;
                    	} catch (Exception e) {
                    		e.printStackTrace();
                    		return;
                    	}
                    }
                }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static BufferedImage updatePic(int[][] newChannel, char channelCode, String path) {
		try {
			BufferedImage bimg = ImageIO.read(new File(path));
			int width = bimg.getWidth();
            int height = bimg.getHeight();
			switch(channelCode) {
				case 'R':
					for(int i=0; i<height; i++)
		                for(int j=0; j<width; j++) {
		                    int rgb = bimg.getRGB(i,j);
		                    int g = rgb&0xff00;
		                    int b = rgb&0xff;
		                    rgb = newChannel[i][j]*256*256 + g + b + 0xff000000; //对R通道更新
		                    bimg.setRGB(i, j, rgb);
		                }
					break;
				case 'G':
					for(int i=0; i<height; i++)
		                for(int j=0; j<width; j++) {
		                    int rgb = bimg.getRGB(i,j);
		                    int r = rgb&0xff0000;
		                    int b = rgb&0xff;
		                    rgb = r + newChannel[i][j]*256 + b + 0xff000000; //对G通道更新
		                    bimg.setRGB(i, j, rgb);
		                }
					break;
				case 'B':
					for(int i=0; i<height; i++)
		                for(int j=0; j<width; j++) {
		                    int rgb = bimg.getRGB(i,j);
		                    rgb = (rgb&0xffffff00) + newChannel[i][j]; //对B通道更新
		                	bimg.setRGB(i, j, rgb);
		                }
					break;
				default:
					break;
			}
			return bimg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void createNewPic(BufferedImage bimg, String newPath) {
		try {
			File file = new File(newPath);
			ImageIO.write(bimg, "bmp", file); //生成隐写后的图片
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int[][] LBSofR(String path, String secretPath) {
		int[][] R = getRData(path); //原图片的R通道
        byte[] secretData = getByteFlow(secretPath); //秘密图片的字节流
        int count = 0;//记录读取到的比特位数
        for(int i=0; i<secretData.length; i++){
        	int a = secretData[i]&0xff;
        	for(int j=0; j<8; j++) {
        		int LB = a&0x1; //依次获取字节的每一位
        		int w = count/R.length;
        		int h = count%R.length;
        		R[w][h] = (R[w][h]&0xfe) + LB; //最低位设置为秘密信息比特流
        		count++;
        		a = a>>1;
        	}
        }
        return R;
	}
	
	public static int[][] LBSofG(String path, String secretPath) {
		int[][] G = getGData(path); //原图片的G通道
        byte[] secretData = getByteFlow(secretPath); //秘密图片的字节流
        int count = 0; //记录读取到的比特位数
        for(int i=0; i<secretData.length; i++){
        	int a = secretData[i]&0xff;
        	for(int j=0; j<8; j++) {
        		int LB = a&0x1; //依次获取字节的每一位
        		int w = count/G.length;
        		int h = count%G.length;
        		G[w][h] = (G[w][h]&0xfe) + LB; //最低位设置为秘密信息比特流
        		count++;
        		a = a>>1;
        	}
        }
        return G;
	}
	
	public static int[][] LBSofB(String path, String secretPath) {
		int[][] B = getBData(path); //原图片的B通道
        byte[] secretData = getByteFlow(secretPath); //秘密图片的字节流
        int count = 0; //记录读取到的比特位数
        for(int i=0; i<secretData.length; i++){
        	int a = secretData[i]&0xff;
        	for(int j=0; j<8; j++) {
        		int LB = a&0x1; //依次获取字节的每一位
        		int w = count/B.length;
        		int h = count%B.length;
        		B[w][h] = (B[w][h]&0xfe) + LB; //最低位设置为秘密信息比特流
        		count++;
        		a = a>>1;
        	}
        }
        return B;
	}
	
	public static byte[] getByteFlow(String path) {
		try {
            BufferedImage bimg = ImageIO.read(new File(path));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bimg, "jpg", out);
            byte[] data = out.toByteArray(); //获得图片的字节流
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
	}
	
	public static int[][] getData(String path) {
        try {
            BufferedImage bimg = ImageIO.read(new File(path));
            int width = bimg.getWidth();
            int height = bimg.getHeight();
            int[][] data = new int[width][height];
            for(int i=0; i<height; i++)
                for(int j=0; j<width; j++)
                    data[i][j] = bimg.getRGB(i,j); //得到图片的位图矩阵
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	public static int[][] getRData(String path) {
        try {
            BufferedImage bimg = ImageIO.read(new File(path));
            int width = bimg.getWidth();
            int height = bimg.getHeight();
            int[][] data = new int[width][height];
            int[][] R = new int[width][height];
            for(int i=0; i<height; i++)
                for(int j=0; j<width; j++) {
                    data[i][j] = bimg.getRGB(i,j); //得到图片的位图矩阵
                    R[i][j] = (data[i][j]&0xff0000)>>16; //得到R通道数据
                }
            return R;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	public static int[][] getGData(String path) {
        try {
            BufferedImage bimg = ImageIO.read(new File(path));
            int width = bimg.getWidth();
            int height = bimg.getHeight();
            int[][] data = new int[width][height];
            int[][] G = new int[width][height];
            for(int i=0; i<height; i++)
                for(int j=0; j<width; j++) {
                    data[i][j] = bimg.getRGB(i,j); //得到图片的位图矩阵
                    G[i][j] = (data[i][j]&0xff00)>>8; //得到G通道数据
                }
            return G;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	public static int[][] getBData(String path) {
        try {
            BufferedImage bimg = ImageIO.read(new File(path));
            int width = bimg.getWidth();
            int height = bimg.getHeight();
            int[][] data = new int[width][height];
            int[][] B = new int[width][height];
            for(int i=0; i<height; i++)
                for(int j=0; j<width; j++) {
                    data[i][j] = bimg.getRGB(i,j); //得到图片的位图矩阵
                    B[i][j] = data[i][j]&0xff; //得到B通道数据
                }
            return B;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
	
}
