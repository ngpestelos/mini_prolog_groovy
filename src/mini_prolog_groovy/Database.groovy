package mini_prolog_groovy

// a stream of facts and rules
class Database implements Iterator {

    def data = []

    def Database() {
        //data << ["male", "bill"]
        //data << ["father", "bill", "jake"]
        data << ["mother", "paul", "mary"]
        data << ["father", "jake", "paul"]
        data << ["father", "paul", "george"]
        data << ["parent", "X", "Y", ["rule" : ["father", "X", "Y"]]]
        data << ["parent", "X", "Y", ["rule" : ["mother", "X", "Y"]]]
        data = data.reverse()
    }

    def next() {
        (data.size() > 0) ? data.pop() : null
    }

    boolean hasNext() {
        data.size() > 0
    }

    void remove() {
        
    }

    static void main(args) {
        def db = new Database()

        println db.next()
        //println db.next()
        //println db.next()
        //println db.next()
    }
	
}

