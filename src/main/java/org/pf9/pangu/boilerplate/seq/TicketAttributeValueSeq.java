package org.pf9.pangu.boilerplate.seq;

import org.pf9.pangu.framework.data.jdbc.util.PgSequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class TicketAttributeValueSeq extends PgSequenceGenerator{

    public TicketAttributeValueSeq(){
        this.setSequence("he_adm_ticket_attribute_value_seq");
    }
}
