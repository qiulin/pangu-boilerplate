package org.pf9.pangu.boilerplate.util;

import com.github.pagehelper.Page;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesQuery;
import org.pf9.pangu.framework.data.domain.datatables.DataTablesResult;

public class DataTablesUtil {

    public static <E> DataTablesResult<E> wrap(Page<E> in, DataTablesQuery req) {

        DataTablesResult<E> out = new DataTablesResult<>();

        out.setData(in.getResult());
        out.setDraw(req.getDraw());
        out.setRecordsTotal(in.getTotal());
        out.setRecordsFiltered(in.getTotal());
        return out;
    }
}
