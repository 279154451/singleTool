package com.single.code.tool.logger;

import java.io.File;

/**
 * 
 * @author yao.guoju
 *
 */
public class LoggerSettings {
	
	public enum Level{
		VERBOSE,
		DEBUG,
		INFO,
		WARNING,
		ERROR,
	}
	
	private Level mLevel;
	private File mLogFile;
	private boolean mSaveFileEnable;
	
	private LoggerSettings(Level level,File file,boolean savefile) {
		this.mLevel = level;
		this.mLogFile = file;
		this.mSaveFileEnable = savefile;
	}

	public Level getLevel() {
		return mLevel;
	}
	
	public File getLogFile() {
		return mLogFile;
	}
	
	public boolean getSaveLogEnable() {
		return mSaveFileEnable;
	}
	
	public static class Builder {
		private Level level;
		private File file;
		private boolean savefile;
		public Builder() {
			
		}
		
		public Builder setLevel(Level l) {
			this.level = l;
			return this;
		}
		
		public Builder setLogFile(File logfile) {
			this.file = logfile;
			return this;
		}
		
		public Builder enableLogFile(boolean enable) {
			this.savefile = enable;
			return this;
		}
		
		public LoggerSettings build() {
			return new LoggerSettings(level, file, savefile);
		}
	}
	
	
}
