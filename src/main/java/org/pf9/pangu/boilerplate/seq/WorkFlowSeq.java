package org.pf9.pangu.boilerplate.seq;

import org.pf9.pangu.framework.data.jdbc.util.PgSequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class WorkFlowSeq extends PgSequenceGenerator{

    public WorkFlowSeq(){
        this.setSequence("he_adm_workstream_seq");

    }
}
