package org.pf9.pangu.boilerplate.seq;

import org.pf9.pangu.framework.data.jdbc.util.PgSequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class FaqSeq extends PgSequenceGenerator {

    public FaqSeq(){
        this.setSequence("he_adm_faq_id_seq");
    }
}
