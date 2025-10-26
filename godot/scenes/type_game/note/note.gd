extends Sprite2D
	
@export var noteType: int	

var linePosition = 0
var lineHeight = 30
const sprite_offset_y = 3.5

func init(type: int) -> void:
	noteType = type
	position = Vector2(0, sprite_offset_y)

func move(x: int, y: int) -> void:
	position += Vector2(x, y + linePosition * lineHeight)

func getNodePositionFromType(type: int) -> void:
	# one octave is an increment of 12 ( 7 white keys and 5 black)
	# Violin clef starts at 60
	var violinBase: int = type - 60
	var octave: int = violinBase / 12 
	var rest: int = violinBase - octave * 12

	if rest == 0 || rest == 1:
		linePosition = octave * 7
	if rest == 2 || rest == 3:
		linePosition = octave * 7 + 1
	if rest == 4:
		linePosition = octave * 7 + 2
	if rest == 5 || rest == 6:
		linePosition = octave * 7 + 3
	if rest ==  7 || rest == 8:
		linePosition = octave * 7 + 4
	if rest == 9 || rest == 10:
		linePosition = octave * 7 + 5
	if rest > 10:
		linePosition = octave * 7 + 6
