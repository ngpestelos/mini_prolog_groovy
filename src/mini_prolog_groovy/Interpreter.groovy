package mini_prolog_groovy

class Interpreter {

    static {
        List.metaClass.car { -> delegate[0] }
        List.metaClass.cdr { -> (delegate.size() >= 2) ? delegate[1..-1] : [] }
    }

    // Evaluates a pattern, given a datum and relevant bindings
    static def match(pattern, datum, binding) {
        println "match ${pattern} ${datum} ${binding}"

        if (isEmpty(pattern) && (isEmpty(datum)))
            return binding

        if (binding == "FAIL")
            return "FAIL"

        if (isEmpty(pattern) && hasRule(datum)) {
            def rule = getRule(datum)
            return applyRule(rule, binding)
        }

        if (isAtom(pattern) && isAtom(datum)) {
            if (isVariable(pattern))
                return bind(pattern, datum, binding)
            else if (isVariable(datum))
                return bind(datum, pattern, binding)
            else if (pattern == datum)
                return binding
            else
                return "FAIL"
        }

        if (isList(pattern) && isList(datum)) {
            def newBinding = match(pattern.car(), datum.car(), binding)
            return match(pattern.cdr(), datum.cdr(), newBinding)
        }

        // unmatched shape, return "FAIL"
        if (isNotEmpty(pattern) || isNotEmpty(datum))
            return "FAIL"
    }

    // A rule is contained in a dictionary
    static def hasRule(datum) {
        if (isEmpty(datum))
            return false

        datum[0] instanceof Map
    }

    static def getRule(pattern) {
        pattern.pop()["rule"]
    }

    static def applyRule(rule, bindings) {
        println "applyRule ${rule} ${bindings}"
        def newRule = substituteVariables(rule, bindings)
        println newRule
        def newBindings = query(newRule, new Database())
        return replaceVariableNames(bindings, newBindings)
    }

    static def query(pattern, database) {
        println "query ${pattern}"

        def res = database.collect { datum ->
            match(pattern, datum, [:])
        }

        res.findAll { it != "FAIL" }
    }

    static def substituteVariables(rule, bindings) {
        bindings.each {k, v ->
            def index = rule.findIndexOf { it == k }
            rule[index] = v
        }

        return rule
    }
    
    static def replaceVariableNames(bindings, newBindings) {        
        newBindings.collect { nb ->

            def map = [:]
            nb.each { k, v ->
                def b = bindings.find { it.getValue() == k }
                map[b.getKey()] = v
            }

            map            
        }
    }

    static def bind(variable, value, binding) {
        binding[variable] = value
        return binding
    }

    static def isVariable(String pattern) {
        pattern ==~ /^[A-Z]$/
    }

    static def isAtom(element) {
        element instanceof String
    }

    static def isList(element) {
        element instanceof List
    }

    static def isEmpty(element) {
        if (element == null)
            return true
        else if ((element instanceof String) && (element == ""))
            return true
        else if ((element instanceof List) && (element.size() == 0))
            return true
        else
            return false
    }

    static def isNotEmpty(element) {
        isEmpty(element) == false
    }

    static void main (args) {        
        println ">>>" + query(["parent", "paul", "Y"], new Database())

        println ">>>" + query(["mother", "paul", "X"], new Database())
    }
	
}

