package app.resources.utils;

import com.opencsv.bean.CsvToBeanFilter;
import com.opencsv.bean.MappingStrategy;

public class Filter implements CsvToBeanFilter {
	private final MappingStrategy<?> strategy;
	Utils utils = new Utils();

	public Filter(MappingStrategy<?> strategy) {
		this.strategy = strategy;
	}

	public boolean allowLine(String[] line) {
		// restore the relevant indices from the strategy
		for (int i = 0; i < line.length; i++) {
			if (utils.verifyValueIsIsNotTooLong(strategy.findField(i).getField().getName())) {
				return false;
			}
		}

		return true;
	}
}
