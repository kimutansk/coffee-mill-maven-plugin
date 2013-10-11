/*
 * Copyright 2013 OW2 Nanoko Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nanoko.coffee.mill.mojos.compile;

import java.util.*;

/**
 * Configuration Object for JavaScriptCompilerMojo.
 * Idem as {@link org.nanoko.coffee.mill.mojos.reporting.JSHintOptions}.
 */
public class JSHintOptions {
    //String format options
    private String options;

    private Boolean bitwise;
    private Boolean camelcase;
    private Boolean curly;
    private Boolean eqeqeq;
    private Boolean es3;
    private Boolean forin;
    private Boolean immed;
    private Boolean indent;
    private Boolean latedef;
    private Boolean newcap;
    private Boolean noarg;
    private Boolean noempty;
    private Boolean nonew;
    private Boolean plusplus;
    private String  quotmark; //single, double or true
    private Boolean undef;
    private Boolean unused;
    private Boolean strict;
    private Boolean trailing;
    private Integer maxparams;
    private Integer maxdepth;
    private Integer maxstatements;
    private Integer maxcomplexity;
    private Integer maxlen;

    public String[] format(){
        Set<String> formatOpts = new HashSet<String>();

        //Parse the string
        if(options !=null && !options.isEmpty()){
            options=options.replaceAll(" ",""); //remove space

            //must match ^\w+(=(\w+))?(,\w+(=(\w+))?)*$
            if(!options.matches("^\\w+(=(\\w+))?(,\\w+(=(\\w+))?)*$")){
                throw new IllegalArgumentException("The jshintOptions options is not formated correctly," +
                    "the format support String such as: <key>,<key>=<value>");
            }

            Collections.addAll(formatOpts, options.split(","));
        }

        if(bitwise != null){
            formatOpts.add("bitwise=" + bitwise.toString());
        }

        if(camelcase != null){
            formatOpts.add("camelcase=" +camelcase.toString());
        }

        if(curly != null){
            formatOpts.add("curly=" +curly.toString());
        }

        if(es3 != null){
            formatOpts.add("es3=" +es3.toString());
        }

        if(forin != null){
            formatOpts.add("forin=" +forin.toString());
        }

        if(immed != null){
            formatOpts.add("immed=" +immed.toString());
        }

        if(indent != null){
            formatOpts.add("indent=" +indent.toString());
        }

        if(latedef != null){
            formatOpts.add("latedef=" +latedef.toString());
        }

        if(newcap != null){
            formatOpts.add("newcap=" +newcap.toString());
        }

        if(noarg != null){
            formatOpts.add("noarg=" +noarg.toString());
        }

        if(noempty != null){
            formatOpts.add("noempty=" +noempty.toString());
        }

        if(nonew != null){
            formatOpts.add("nonew=" +nonew.toString());
        }

        if(plusplus != null){
            formatOpts.add("plusplus=" +plusplus.toString());
        }

        if(quotmark != null){
            if(!(quotmark.equals("single") || quotmark.equals("double") || quotmark.equals("true"))){
                throw new IllegalArgumentException("The jshintOptions quotmark must have the value single,double or true");
            }
            formatOpts.add("quotmark=" +quotmark);
        }

        if(undef != null){
            formatOpts.add("undef=" + undef.toString());
        }

        if(unused != null){
            formatOpts.add("unused=" + unused.toString());
        }

        if(strict != null){
            formatOpts.add("strict=" + strict.toString());
        }

        if(trailing != null){
            formatOpts.add("trailing=" + trailing.toString());
        }

        if(maxparams != null){
            formatOpts.add("maxparams=" + maxparams.toString());
        }

        if(maxdepth != null){
            formatOpts.add("maxdepth=" + maxdepth.toString());
        }

        if(maxstatements != null){
            formatOpts.add("maxstatements=" + maxstatements.toString());
        }

        if(maxcomplexity != null){
            formatOpts.add("maxcomplexity=" + maxcomplexity.toString());
        }

        if(maxlen != null){
            formatOpts.add("maxlen=" + maxlen.toString());
        }

        return formatOpts.toArray(new String[formatOpts.size()]);
    }
}
