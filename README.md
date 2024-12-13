# frogger

## Frogger syntax
### Basic movement:
- left val
- right val
- down val
- up val
- rotate val;

examples: 
- left 1; - moves to the left by 1
- down 20; - moves done by 20
- rotate 90; - rotates clockwise by 90 degrees

### Other commands:
- pen up
- pen down
- color [string color]

examples:
- pen down; puts the pen down to draw
- color green; sets the color to green



### Structures:
- for(int val){statement}
- if(x/y){statement}

examples:
- for(10){down 10;} - executes down 10 times
- if(x < 90) {rotate 5;} - if x coordinate is less than 90, rotates the frog clockwise 5 degress

Supports nested structures like for(val){if(y > val){for(5){statement}}}
