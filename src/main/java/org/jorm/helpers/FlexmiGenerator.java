package org.jorm.helpers;

import org.jorm.dto.*;
import org.jorm.dto.Class;

public class FlexmiGenerator {
    public static String generate(Class c){
        String output = new String("<?nsuri class?>\n<Class name=\""+c.name+"\">\n");

        output+= "<fields>\n";
        for(Field f :c.fields ){
            output+="<Field name=\""+f.name+"\"";if(f.uq)output +=" uq = \""+f.uq+"\""; if(f.nullable)output +=" nullable = \""+f.nullable+"\""; output+= ">\n";
            output+="<type>"+f.type+"</type>";
            output+="</Field>\n";
        }
        output+= "</fields>\n";
        output+= "</Class>\n";
        return output;
    }
}
