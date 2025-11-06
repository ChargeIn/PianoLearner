extends Sprite2D
	
@export var noteType: int
@export var isBassNote: bool

@export var helper_line: PackedScene = preload("res://scenes/type_game/note/helper_line.tscn")

var linePosition = 0
var lineHeight = 10.5 # line height 18 px  + 3 px line it self  => 21px / 2
const zero_position_violin = 384
const zero_position_bass = 258

var helper_lines = []

func init(type: int, is_bass_note: bool) -> void:
	noteType = type
	isBassNote = is_bass_note
	initNoteFromeType(type, is_bass_note)

func move(x: int, y: int) -> void:
	position += Vector2(x, y - linePosition * lineHeight)

func initNoteFromeType(type: int, is_bass: bool) -> void:
	# one octave is an increment of 12 ( 7 white keys and 5 black)
	var violinBase: int = type
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
	
	position = Vector2(0, zero_position_bass if is_bass else zero_position_violin)
	
	# spawn helper lines if needed
	if is_bass:
		if type >= 64:
			spawnHelperLine(0)
			spawnHelperLine(2)
		elif type >= 62:
			spawnHelperLine(1)
		elif type >= 60:
			spawnHelperLine(0)
		if type <= 34:
			spawnHelperLine(-4)
			spawnHelperLine(-2)
			spawnHelperLine(0)
		elif type <= 35:
			spawnHelperLine(-3)
			spawnHelperLine(-1)
		elif type <= 37:
			spawnHelperLine(-2)
			spawnHelperLine(0)
		elif type <= 39:
			spawnHelperLine(-1)
		elif type <= 40: 
			spawnHelperLine(0)
	else:
		if type >= 84:
			spawnHelperLine(0)
			spawnHelperLine(2)
		elif type >= 83:
			spawnHelperLine(1)
		elif type >= 81:
			spawnHelperLine(0)
		if type <= 54:
			spawnHelperLine(-4)
			spawnHelperLine(-2)
			spawnHelperLine(0)
		elif type <= 56:
			spawnHelperLine(-3)
			spawnHelperLine(-1)
		elif type <= 58:
			spawnHelperLine(-2)
			spawnHelperLine(0)
		elif type <= 59:
			spawnHelperLine(-1)
		elif type <= 61:
			spawnHelperLine(0)
	

func spawnHelperLine(line: int) -> void:
	var sprite: Sprite2D = helper_line.instantiate()
	sprite.position = Vector2(-12, 46.5 + line * lineHeight)
	helper_lines.push_back(sprite)
	add_child(sprite)
	
func clearHelperLines() -> void:
	for line in helper_lines:
		remove_child(line)
	
