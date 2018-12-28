package org.shekinah.web.generic;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.shekinah.domain.BaseEntity;
import org.shekinah.service.BaseService;

public class GenericLazyDataModel<T extends BaseEntity> extends LazyDataModel<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private List<T> data;
    private BaseService<T> service;
    
    public GenericLazyDataModel(BaseService<T> service) {
        this.service = service;
    }
    
    @Override
    public T getRowData(String rowKey) {
        for(T entry : data) {
            if(entry.getId().toString().equals(rowKey))
                return entry;
        }
        return null;
    }

    @Override
    public Object getRowKey(T entry) {
        return entry.getId();
    }

    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        
        data = service.findEntriesPagedAndFilteredAndSorted(first, pageSize, sortField, sortOrder, filters);
        
        this.setRowCount((int) service.countAllEntries());

        return data;
    }
}
                    
