/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2018 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.base.web;

import java.util.LinkedHashMap;

import com.axelor.apps.base.db.ImportHistory;
import com.axelor.apps.base.service.imports.ImportCityService;
import com.axelor.exception.service.TraceBackService;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaFileRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ImportCityController {
	
	@Inject 
	private ImportCityService importCityService;
	
	/**
	 * Import cities
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void importCity(ActionRequest request,ActionResponse response) {
		
		String typeSelect = (String) request.getContext().get("typeSelect");
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) request.getContext().get("metaFile");
		MetaFile dataFile = Beans.get(MetaFileRepository.class).find(((Integer)map.get("id")).longValue());
		
		try {
			ImportHistory importHistory = importCityService.importCity(typeSelect, dataFile);
			response.setAttr("importHistoryList", "value:add", importHistory);
			response.setNotify( importHistory.getLog().replaceAll("(\r\n|\n\r|\r|\n)", "<br />"));
			
		} catch (Exception e) {
			TraceBackService.trace( response, e ); 
		}
	}
}
