/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * =============================================================================
 */
package org.ikasan.common.security.policy;

import org.ikasan.common.security.algo.Blowfish;
import org.ikasan.common.security.algo.PBE;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * This class provides XStream converter for a <code>Policy</code> class.
 *
 * @author Ikasan Development Team
 */
public class EncryptionPolicyConverter
    implements Converter
{
    
    /**
     * Creates a new <code>EncryptionPolicyConverter</code> instance.
     *
     */
    public EncryptionPolicyConverter()
    {
        // Do Nothing
    }

    /**
     * Converts an object to XML on the assumption that all class instance vars
     * will become XML attributes. If this is not the case then this method 
     * should be overridden.
     * @param object 
     * @param writer 
     * @param context 
     */
    public void marshal(Object object, HierarchicalStreamWriter writer,
                        MarshallingContext context)
    {
        EncryptionPolicy policy = (EncryptionPolicy)object;

        // policy name
        if (policy.getName() != null)
        {
            writer.addAttribute("name", policy.getName());
        }
        
        // algorithm
        if (policy.getAlgorithm() != null)
        {
            writer.startNode(policy.getAlgorithm().getClass().getSimpleName());
            context.convertAnother(policy.getAlgorithm());
            writer.endNode();
        }
    }

    /**
     * Converts textual data back into an object.
     * @param reader 
     * @param context 
     * @return EncryptionPolicy
     */
    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context)
    {
        EncryptionPolicy policy = new EncryptionPolicy();

        String attrValue = null;

        // policy name
        attrValue = reader.getAttribute("name");
        policy.setName(attrValue);

        String nodeName = null;
        while (reader.hasMoreChildren())
        {
            reader.moveDown();
            nodeName = reader.getNodeName();

            // supported algorithms
            if (nodeName.equals("PBE"))
            {
                PBE pbe = (PBE)context.convertAnother(policy, PBE.class);
                policy.setAlgorithm(pbe);
            }
            // Return System
            else
            if (nodeName.equals("Blowfish"))
            {
                Blowfish blowfish = (Blowfish)context.convertAnother(policy, Blowfish.class);
                policy.setAlgorithm(blowfish);
            }

            reader.moveUp();
        }

        return policy;
    }

    /**
     * Determines whether the converter can marshal a particular class or
     * derivation thereof.
     * 
     * NOTE:  The method parameter type is forced to be a raw type of Class by its parent
     * 
     * @param type 
     * @return true if we can convert, else false
     */
    @SuppressWarnings("unchecked")
    public boolean canConvert(final Class type)
    {
        return type.equals(EncryptionPolicy.class);
    }
}
