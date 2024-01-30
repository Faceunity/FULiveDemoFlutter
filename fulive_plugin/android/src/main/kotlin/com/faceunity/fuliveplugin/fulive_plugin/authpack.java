package com.faceunity.fuliveplugin.fulive_plugin;

import java.security.MessageDigest;

public class authpack {
	public static int sha1_32(byte[] buf){int ret=0;try{byte[] digest=MessageDigest.getInstance("SHA1").digest(buf);return ((int)(digest[0]&0xff)<<24)+((int)(digest[1]&0xff)<<16)+((int)(digest[2]&0xff)<<8)+((int)(digest[3]&0xff)<<0);}catch(Exception e){}return ret;}
	public static byte[] A(){
        //#error 请先联系相芯科技替换鉴权文件
        return ;
    }
}
