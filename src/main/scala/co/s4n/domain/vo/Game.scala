package co.s4n.domain.vo

class Game(val id : Int, val name : String) {
    override def toString() : String = {
        f"""id: ${this.id}
name: ${this.name}"""
    }
}

object Game {
    def apply(id: Int, name : String) : Game = new Game(id, name)
}