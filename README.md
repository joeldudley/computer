TODOs:

* Wide chips:
    * Parser nodes have been modified
    * In generator, need to:
        * Establish rhs width based on input/output width
        * Check rhs width matches width of thing it is being assigned to
        * Extend Chip to map strings to lists of gates
        * Extend evaluator to work with wide inputs/outputs
        * Provide an interface for passing in wide inputs/outputs
* Screen and keyboard
* Larger set of pre-defined chips
* Use of @before where possible