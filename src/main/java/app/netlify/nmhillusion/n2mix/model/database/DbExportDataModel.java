package app.netlify.nmhillusion.n2mix.model.database;

import java.util.List;

public class DbExportDataModel {
	private List<String> header;
	
	private List<List<String>> values;

	public List<String> getHeader() {
		return header;
	}

	public DbExportDataModel setHeader(List<String> header) {
		this.header = header;
		return this;
	}

	public List<List<String>> getValues() {
		return values;
	}

	public DbExportDataModel setValues(List<List<String>> values) {
		this.values = values;
		return this;
	}
	
	

}
