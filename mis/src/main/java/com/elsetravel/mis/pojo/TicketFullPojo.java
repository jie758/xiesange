package com.elsetravel.mis.pojo;

import com.elsetravel.gen.dbentity.ticket.Ticket;
import com.elsetravel.gen.dbentity.ticket.TicketMain;

public class TicketFullPojo {
	private TicketMain main;
	private Ticket ticket;
	
	public TicketFullPojo(TicketMain main,Ticket ticket){
		this.main = main;
		this.ticket = ticket;
	}

	public TicketMain getMain() {
		return main;
	}

	public void setMain(TicketMain main) {
		this.main = main;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}
}
