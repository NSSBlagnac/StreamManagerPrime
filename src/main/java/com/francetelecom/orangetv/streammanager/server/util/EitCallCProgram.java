package com.francetelecom.orangetv.streammanager.server.util;

import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel;

/**
 * Appelle le program en C pour lui fournir les nouvelles Eit
 * - compile javac
 * - create header javah ./include
 * 
 * Program C
 * - implement source from header
 * - compile >> *.o
 * - generate dll >> gcc
 * (pour windows : gcc -Wl,--add-stdcall-alias -I%JAVA_HOME%\include -I%JAVA_HOME%\include\win32 -shared -o eitinjector.dll main.c eitreceiver.c jni_utils.c)
 *
 * Program Java
 * - executer avec -Djava.library.path=<repertoire contenant dll>
 * @author sylvie
 *
 */
public class EitCallCProgram {
	
	static {
		  // Load native library at runtime
		  // hello.dll (Windows) or libhello.so (Unixes)
		  // This library shall be included in Java's library path
		  //  You could include the library into Java Library's path via VM argument -Djava.library.path=path_to_lib.
		    System.loadLibrary("eitinjector");

	  }
	
	private final static EitCallCProgram instance = new EitCallCProgram();
	public static EitCallCProgram get() {
		return instance;
	}
	
	  // Declare a native methode sendEit() that receives an EitInfoModel and returns void
	public native boolean sendEit(EitInfoModel eitInfoModel);
	

}
