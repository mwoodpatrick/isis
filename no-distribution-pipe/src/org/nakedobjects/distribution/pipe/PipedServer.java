package org.nakedobjects.distribution.pipe;

import org.nakedobjects.distribution.ServerDistribution;
import org.nakedobjects.distribution.SingleResponseUpdateNotifier;
import org.nakedobjects.distribution.UpdatePackager;
import org.nakedobjects.distribution.command.Request;
import org.nakedobjects.distribution.command.Response;

import org.apache.log4j.Logger;


public class PipedServer {
    private static final Logger LOG = Logger.getLogger(PipedServer.class);
    private ServerDistribution facade;
    private PipedConnection communication;
    private SingleResponseUpdateNotifier updateNotifier;

    public synchronized void run() {
        while (true) {
            Request request = communication.getRequest();
            LOG.debug("client request: " + request);
            
            UpdatePackager updates = updateNotifier.createUpdatePackager();
            
            request.execute(facade);
            LOG.debug("server updates: " + updates.updateList());
            
		    Response response = new Response(request);
            LOG.debug("server response: " + response);
            
            response.setUpdates(updates.getUpdates());
		    
            communication.setResponse(response);
        }

    }

    public void setConnection(PipedConnection communication) {
        this.communication = communication;
    }
    
    public void setFacade(ServerDistribution facade) {
        this.facade = facade;
    }

    public void setUpdateNotifier(SingleResponseUpdateNotifier updateNotifier) {
        this.updateNotifier = updateNotifier;
    }
}

/*
 * Naked Objects - a framework that exposes behaviourally complete business
 * objects directly to the user. Copyright (C) 2000 - 2005 Naked Objects Group
 * Ltd
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The authors can be contacted via www.nakedobjects.org (the registered address
 * of Naked Objects Group is Kingsway House, 123 Goldworth Road, Woking GU21
 * 1NR, UK).
 */