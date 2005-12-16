package test.org.nakedobjects.objects.specification;

import org.nakedobjects.object.NakedObjectSpecification;
import org.nakedobjects.object.NakedObjectSpecificationLoader;
import org.nakedobjects.object.reflect.internal.NullSpecification;
import org.nakedobjects.utility.DebugString;

import java.util.Enumeration;
import java.util.Hashtable;

import test.org.nakedobjects.objects.bom.Movie;
import test.org.nakedobjects.objects.bom.Person;

public class TestSpecificationLoader implements NakedObjectSpecificationLoader {
    private Hashtable specs = new Hashtable();
       
    public NakedObjectSpecification loadSpecification(String name) {
        NakedObjectSpecification specification = (NakedObjectSpecification) specs.get(name);
        if(specification == null) {
//            throw new NakedObjectRuntimeException("No specification for " + name);
            return new NullSpecification(name);
        }
        return specification;
    }

    public NakedObjectSpecification loadSpecification(Class cls) {
        return loadSpecification(cls.getName());
    }

    public NakedObjectSpecification[] allSpecifications() {
        NakedObjectSpecification[] array = new NakedObjectSpecification[specs.size()];
        Enumeration e = specs.elements();
        int i = 0;
        while (e.hasMoreElements()) {
            array[i++] = (NakedObjectSpecification) e.nextElement();
        }
        return array;
    }

    public void init() {
        specs.put(Movie.class.getName(), new MovieSpecification());
        specs.put(Person.class.getName(), new PersonSpecification());
    }

    public void shutdown() {
        specs.clear();
    }

    public String getDebugData() {
        DebugString str = new DebugString();
        NakedObjectSpecification[] list = allSpecifications();
        for (int i = 0; i < list.length; i++) {
            str.appendln(list[i].getFullName());
        }
        return str.toString();
    }

    public String getDebugTitle() {
        return "Test Specification Loader";
    }

}


/*
 * Naked Objects - a framework that exposes behaviourally complete business objects directly to the user.
 * Copyright (C) 2000 - 2005 Naked Objects Group Ltd
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The authors can be contacted via www.nakedobjects.org (the registered address of Naked Objects Group is
 * Kingsway House, 123 Goldworth Road, Woking GU21 1NR, UK).
 */