package org.pf9.pangu.boilerplate.seq;

import org.pf9.pangu.framework.data.jdbc.util.PgSequenceGenerator;
import org.springframework.stereotype.Component;

@Component
public class TicketAssocSeq extends PgSequenceGenerator{

    public TicketAssocSeq(){
        this.setSequence("he_adm_ticket_assoc_seq");
    }

}
