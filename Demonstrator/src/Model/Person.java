package Model;

public class Person{
	 private String edaFileAbsPath;
	 private String logFileAbsPath;
	
	 public Person(){}
	 
	 public Person(String edaFile,String logFile){
		 this.edaFileAbsPath = edaFile;
		 this.logFileAbsPath = logFile;
	 }
	 
	 public String getEdaFileAbsPath() {
		return edaFileAbsPath;
	}
	public void setEdaFileAbsPath(String edaFileAbsPath) {
		this.edaFileAbsPath = edaFileAbsPath;
	}
	public String getLogFileAbsPath() {
		return logFileAbsPath;
	}
	public void setLogFileAbsPath(String logFileAbsPath) {
		this.logFileAbsPath = logFileAbsPath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edaFileAbsPath == null) ? 0 : edaFileAbsPath.hashCode());
		result = prime * result + ((logFileAbsPath == null) ? 0 : logFileAbsPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		if (edaFileAbsPath == null) {
			if (other.edaFileAbsPath != null)
				return false;
		} else if (!edaFileAbsPath.equals(other.edaFileAbsPath))
			return false;
		if (logFileAbsPath == null) {
			if (other.logFileAbsPath != null)
				return false;
		} else if (!logFileAbsPath.equals(other.logFileAbsPath))
			return false;
		return true;
	}
}