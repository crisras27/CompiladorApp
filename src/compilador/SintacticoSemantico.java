/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: ___________    HORA: ___________ HRS
 *:                                   
 *:               
 *:         Clase con la funcionalidad del Analizador Sintactico
 *                 
 *:                           
 *: Archivo       : SintacticoSemantico.java
 *: Autor         : Fernando Gil  ( Estructura general de la clase  )
 *:                 Grupo de Lenguajes y Automatas II ( Procedures  )
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   : Esta clase implementa un parser descendente del tipo 
 *:                 Predictivo Recursivo. Se forma por un metodo por cada simbolo
 *:                 No-Terminal de la gramatica mas el metodo emparejar ().
 *:                 El analisis empieza invocando al metodo del simbolo inicial.
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 22/Feb/2015 FGil                -Se mejoro errorEmparejar () para mostrar el
 *:                                 numero de linea en el codigo fuente donde 
 *:                                 ocurrio el error.
 *: 08/Sep/2015 FGil                -Se dejo lista para iniciar un nuevo analizador
 *:                                 sintactico.
 *: 08/Sep/2017 FGil                - Cambiar el token opasig por opasigna. 
 *: 20/Feb/2018 FGil                - Preparar codigo para usarlo en el sem EJ/2018
 *:----------------------------------------------------------------------------
 */
package compilador;

import javax.swing.JOptionPane;

public class SintacticoSemantico {

    private Compilador cmp;
    private boolean analizarSemantica = false;
    private String preAnalisis;

    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
    public SintacticoSemantico(Compilador c) {
        cmp = c;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // Metodo que inicia la ejecucion del analisis sintactico predictivo.
    // analizarSemantica : true = realiza el analisis semantico a la par del sintactico
    //                     false= realiza solo el analisis sintactico sin comprobacion semantica
    public void analizar(boolean analizarSemantica) {
        this.analizarSemantica = analizarSemantica;
        preAnalisis = cmp.be.preAnalisis.complex;

        // * * *   INVOCAR AQUI EL PROCEDURE DEL SIMBOLO INICIAL   * * *
        programa();
    }

    //--------------------------------------------------------------------------
    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
            preAnalisis = cmp.be.preAnalisis.complex;
        } else {
            errorEmparejar(t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------
    private void errorEmparejar(String _token, String _lexema, int numLinea) {
        String msjError = "";

        if (_token.equals("id")) {
            msjError += "Se esperaba un identificador";
        } else if (_token.equals("num")) {
            msjError += "Se esperaba una constante entera";
        } else if (_token.equals("num.num")) {
            msjError += "Se esperaba una constante real";
        } else if (_token.equals("literal")) {
            msjError += "Se esperaba una literal";
        } else if (_token.equals("oparit")) {
            msjError += "Se esperaba un operador aritmetico";
        } else if (_token.equals("oprel")) {
            msjError += "Se esperaba un operador relacional";
        } else if (_token.equals("opasigna")) {
            msjError += "Se esperaba operador de asignacion";
        } else {
            msjError += "Se esperaba " + _token;
        }
        msjError += " se encontró " + (_lexema.equals("$") ? "fin de archivo" : _lexema)
                + ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

        cmp.me.error(Compilador.ERR_SINTACTICO, msjError);
    }

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico
    private void error(String _descripError) {
        cmp.me.error(cmp.ERR_SINTACTICO, _descripError);
    }

    // Fin de error
    //--------------------------------------------------------------------------
    //  *  *   *   *    PEGAR AQUI EL CODIGO DE LOS PROCEDURES  *  *  *  *
    //--------------------------------------------------------------------------
    // Autor: Alejandro del Rio Ledesma
    private void programa() {
        if (preAnalisis.equals("program")) {
            //programa -> program id ( input, output ) ; declaraciones declaraciones_subprogramas proposicion_compuesta
            emparejar("program");
            emparejar("id");
            emparejar("(");
            emparejar("input");
            emparejar(",");
            emparejar("output");
            emparejar(")");
            emparejar(";");
            declaraciones();
            declaraciones_subprogramas();
            proposicion_compuesta();
            emparejar(".");
        } else {
            error("Se esperaba el inicio del programa (program)");
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Alejandro del Rio Ledesma
    private void lista_identificadores() {
        if (preAnalisis.equals("id")) {
            //lista_identificadores -> id lista_identificadoresP
            emparejar("id");
            lista_identificadoresP();
        } else {
            error("se esperaba un identificador");
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Alejandro del Rio Ledesma
    private void lista_identificadoresP() {
        if (preAnalisis.equals(",")) {
            //lista_identificadoresP -> , id lista_identificadoresP
            emparejar(",");
            emparejar("id");
            lista_identificadoresP();
        } else {
            //lista_identificadoresP -> empty
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Alejandro del Rio Ledesma
    private void declaraciones() {
        if (preAnalisis.equals("var")) {
            //declaraciones -> var lista_identificadores : tipo ; declaraciones
            emparejar("var");
            lista_identificadores();
            emparejar(":");
            tipo();
            emparejar(";");
            declaraciones();
        } else {
            //declaraciones -> empty
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Alejandro del Rio Ledesma
    private void tipo() {
        if (preAnalisis.equals("integer")
                || preAnalisis.equals("real")) {
            //tipo -> tipo_estandar
            tipo_estandar();
        } else if (preAnalisis.equals("array")) {
            //tipo -> array [ num..num ] of tipo_estandar
            emparejar("array");
            emparejar("[");
            emparejar("num");
            emparejar("..");
            emparejar("num");
            emparejar("]");
            emparejar("of");
            tipo_estandar();
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Alejandro del Rio Ledesma
    private void tipo_estandar() {
        if (preAnalisis.equals("integer")) {
            //tipo_estandar -> integer
            emparejar("integer");
        } else if (preAnalisis.equals("real")) {
            //tipo_estandar -> real
            emparejar("real");
        } else {
            error("se esperaba integer o real");
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Alejandro del Rio Ledesma
    private void declaraciones_subprogramas() {
        if (preAnalisis.equals("function")
                || preAnalisis.equals("procedure")) {
            //declaraciones_subprogramas -> declaraciones_subprogramasP
            declaraciones_subprogramasP();
        } else {
            //declaraciones_subprogramas -> empty
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Alejandro del Rio Ledesma
    private void declaraciones_subprogramasP() {
        if (preAnalisis.equals("function")
                || preAnalisis.equals("procedure")) {
            //declaraciones_subprogramasP -> declaracion_subprograma ; declaraciones_subprogramaP
            declaracion_subprograma();
            emparejar(";");
            declaraciones_subprogramasP();
        } else {
            //declaraciones_subprogramasP -> empty
        }
    }

    private void declaracion_subprograma() {
        if (preAnalisis.equals("function")
                || preAnalisis.equals("procedure")) {
            //declaracion_subprograma -> encab_subprograma declaraciones proposicion_compuesta
            encab_subprograma();
            declaraciones();
            proposicion_compuesta();
        } else {
            error("se esperaba function o procedure");
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Alejandro del Rio Ledesma
    private void encab_subprograma() {
        if (preAnalisis.equals("function")) {
            //encab_subprograma -> function id argumentos : tipo_estandar ;
            emparejar("function");
            emparejar("id");
            argumentos();
            emparejar(":");
            tipo_estandar();
            emparejar(";");
        } else if (preAnalisis.equals("procedure")) {
            //encab_subprograma -> procedure id argumentos ;
            emparejar("procedure");
            emparejar("id");
            argumentos();
            emparejar(";");
        }
    }
//---------------------------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------20/02/2018------------------------------------------------------------------------------------------------

    private void argumentos() {
        if (preAnalisis.equals("(")) {
            //argumentos -> ( lista_parametros ) 
            emparejar("(");
            lista_parametros();
            emparejar(")");
        } else {
            //argumentos ->empty
        }
    }

    private void lista_parametros() {
        //lista_parametros -> lista_identificadores  :  tipo  lista_parametros’
        if (preAnalisis.equals("id")) {
            lista_identificadores();
            emparejar(":");
            tipo();
            lista_parametrosP();
        }
    }

    private void lista_parametrosP() {
        //lista_parametros’ -> ; lista_identificadores  :  tipo lista_parametros’ | ϵ 
        if (preAnalisis.equals(";")) {
            emparejar(";");
            lista_identificadores();
            emparejar(":");
            tipo();
            lista_parametrosP();
        } else {
            //lista_parametros’ -> ϵ
        }
    }

    private void proposicion_compuesta() {
        //proposicion_compuesta -> begin proposiciones_optativas  end
        if (preAnalisis.equals("begin")) {
            emparejar("begin");
            proposiciones_optativas();
            emparejar("end");
        }
    }

    private void proposiciones_optativas() {
        //proposiciones_optativas -> lista_proposiciones  | ϵ 
        if (preAnalisis.equals("id") || preAnalisis.equals("begin")
                || preAnalisis.equals("if") || preAnalisis.equals("while")) {
            lista_proposiciones();
        } else {
            //proposiciones_optativas -> ϵ
        }
    }

    private void lista_proposiciones() {
        //lista_proposiciones -> proposicion lista_proposiciones’
        if (preAnalisis.equals("id") || preAnalisis.equals("begin")
                || preAnalisis.equals("if") || preAnalisis.equals("while")) {
            proposicion();
            lista_proposicionesP();
        }
    }

    private void lista_proposicionesP() {
        //lista_proposiciones’ -> ;  proposicion lista_proposiciones’  | ϵ 
        if (preAnalisis.equals(";")) {
            emparejar(";");
            proposicion();
            lista_proposicionesP();
        } else {
            //lista_proposiciones’ -> ϵ
        }
    }

    private void proposicion() {
        //proposicion -> id  proposicion’ | proposicion_compuesta | 
        //			     if expresion then proposicion else proposicion  | while expresion do proposicion 
        if (preAnalisis.equals("id")) {
            emparejar("id");
            proposicionP();
        } else if (preAnalisis.equals("begin")) {
            proposicion_compuesta();
        } else if (preAnalisis.equals("if")) {
            emparejar("if");
            expresion();
            emparejar("then");
            proposicion();
            emparejar("else");
            proposicion();
        } else if (preAnalisis.equals("while")) {
            emparejar("while");
            expresion();
            emparejar("do");
            proposicion();
        }
    }

    private void proposicionP() {
        //proposicion’ -> variable opasigna expresion | opasigna expresion |
        //			      proposicion_procedimiento | ϵ
        if (preAnalisis.equals("[")) {
            variable();
            emparejar("opasigna");
            expresion();
        } else if (preAnalisis.equals("opasigna")) {
            emparejar("opasigna");
            expresion();
        } else if (preAnalisis.equals("(")) {
            proposicion_procedimiento();
        } else {

        }
    }

    private void variable() {
        if (preAnalisis.equals("[")) {
            emparejar("[");
            expresion();
            emparejar("]");
        }
    }

    private void proposicion_procedimiento() {
        if (preAnalisis.equals("(")) {
            proposicion_procedimientoP();
        }
    }

    private void proposicion_procedimientoP() {
        if (preAnalisis.equals("(")) {
            emparejar("(");
            lista_expresiones();
            emparejar(")");
        } else {

        }
    }

    private void lista_expresiones() {
        if (preAnalisis.equals("id") || preAnalisis.equals("num")
                || preAnalisis.equals("num.num") || preAnalisis.equals("(")) {
            expresion();
            lista_expresionesP();
        }
    }

    private void lista_expresionesP() {
        if (preAnalisis.equals(",")) {
            emparejar(",");
            lista_expresiones();
        } else {

        }
    }

    private void expresion() {
        if (preAnalisis.equals("id") || preAnalisis.equals("num")
                || preAnalisis.equals("num.num") || preAnalisis.equals("(")) {
            expresion_simple();
            expresionP();
        }
        else{
            error("se esperaba id , num, num.num O (");
        }
    }

    private void expresionP() {
        if (preAnalisis.equals("oprel")) {
            emparejar("oprel");
            expresion_simple();
        } else {

        }
    }

    private void expresion_simple() {
        if (preAnalisis.equals("id") || preAnalisis.equals("num")
                || preAnalisis.equals("num.num") || preAnalisis.equals("(")) {
            termino();
            expresion_simpleP();
        }
    }

    private void expresion_simpleP() {
        if (preAnalisis.equals("opsuma")) {
            emparejar("opsuma");
            termino();
            expresion_simpleP();
        } else {

        }
    }

    private void termino() {
        if ( preAnalisis.equals("id") || preAnalisis.equals("num")
                || preAnalisis.equals("num.num") || preAnalisis.equals("(") ) {
            factor();
            terminoP();
        }
    }

    private void terminoP() {
        if (preAnalisis.equals("opmult")) {
            emparejar("opmult");
            factor();
            terminoP();

        } else {

        }
    }

    private void factor() {
        if (preAnalisis.equals("id")) {
            emparejar("id");
            factorP();
        } else if (preAnalisis.equals("num")) {
            emparejar("num");
        } else if (preAnalisis.equals("num.num")) {
            emparejar("num.num");
        } else if (preAnalisis.equals("(")) {
            emparejar("(");
            expresion();
            emparejar(")");
        }
    }

    private void factorP() {
        if (preAnalisis.equals("(")) {
            emparejar("(");
            lista_expresiones();
            emparejar(")");
        } else {

        }
    }

}

//------------------------------------------------------------------------------
//::
