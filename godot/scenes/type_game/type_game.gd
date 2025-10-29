extends Control

@onready var note_scene = preload("res://scenes/type_game/note/note.tscn")
@onready var note_spawn = %NoteSpawn
@onready var note_name_label: Label = %NoteName

enum GameMode {
	WHITE_NOTES_ONLY,
	ALL_NOTES,
}

const LOWEST_VIOLIN_NOTE = 57

var spawn_range = 24
var last_spwaned_note = -1
var game_mode = GameMode.WHITE_NOTES_ONLY

var spawned_notes = []

func _ready() -> void:
	PianoManager.note_pressed.connect(_note_pressed)
	_spawn_note()

func _spawn_note() -> void:
	for n in spawned_notes:
		remove_child(n)
	
	spawned_notes.clear()
	
	var new_note = note_scene.instantiate()
	new_note.init(generate_note())
	new_note.move(note_spawn.position.x - size.x / 2, note_spawn.position.y)
	call_deferred("add_child", new_note)
	spawned_notes.push_back(new_note)
	
	note_name_label.text = note_names[new_note.noteType]

func _note_pressed(note: int) -> void:
	if spawned_notes.size() == 1 && spawned_notes.get(0).noteType == note:
		_spawn_note()

func generate_note() -> int:
	var note = LOWEST_VIOLIN_NOTE
	
	if game_mode == GameMode.WHITE_NOTES_ONLY:
		note = LOWEST_VIOLIN_NOTE + randi_range(0, spawn_range)
		
		while is_black_note(note) || note == last_spwaned_note:
			note = LOWEST_VIOLIN_NOTE + randi_range(0, spawn_range)

	else:
		note += randi_range(0, spawn_range)
		
		while note == last_spwaned_note:
			note += randi_range(0, spawn_range)
	
	last_spwaned_note = note
	return note

func is_black_note(note: int) -> bool:
	var base = note % 12
	return base == 1 || base == 3 || base == 6 || base == 8 || base == 10





# list of all midi notes and their names
const note_names = {
	127:"G⁹",
	126:"F#⁹ / Gb⁹",
	125:"F⁹",
	124:"E⁹",
	123:"D#⁹ / Eb⁹",
	122:"D⁹",
	121:"C#⁹ / Db⁹",
	120:"C⁹",
	119:"B⁸",
	118:"A#⁸ / Bb⁸",
	117:"A⁸",
	116:"G#⁸ / Ab⁸",
	115:"G⁸",
	114:"F#⁸ / Gb⁸",	
	113:"F⁸",
	112:"E⁸",
	111:"D#⁸ / Eb⁸",
	110:"D⁸",	
	109:"C#⁸ / Db⁸",
	108:"C⁸",
	107:"B⁷",
	106:"A#⁷ / Bb⁷",
	105:"A⁷",
	104:"G#⁷ / Ab⁷",
	103:"G⁷",
	102:"F#⁷ / Gb⁷",
	101:"F⁷",
	100:"E⁷",
	99:"D#⁷ / Eb⁷",
	98:"D⁷",
	97:"C#⁷ / Db⁷",
	96:"C⁷",
	95:"B⁶",
	94:"A#⁶ / Bb⁶",
	93:"A⁶",
	92:"G#⁶ / Ab⁶",
	91:"G⁶",
	90:"F#⁶ / Gb⁶",
	89:"F⁶",
	88:"E⁶",
	87:"D#⁶ / Eb⁶",
	86:"D⁶",
	85:"C#⁶ / Db⁶",
	84:"C⁶",
	83:"B⁵",
	82:"A#⁵ / Bb⁵",
	81:"A⁵",
	80:"G#⁵ / Ab⁵",
	79:"G⁵",
	78:"F#⁵ / Gb⁵",
	77:"F⁵",
	76:"E⁵",
	75:"D#⁵ / Eb⁵",
	74:"D⁵",
	73:"C#⁵ / Db⁵",
	72:"C⁵",
	71:"B⁴",
	70:"A#⁴ / Bb⁴",
	69:"A⁴",
	68:"G#⁴ / Ab⁴",
	67:"G⁴",
	66:"F#⁴ / Gb⁴",
	65:"F⁴",
	64:"E⁴",
	63:"D#⁴ / Eb⁴",
	62:"D⁴",
	61:"C#⁴ / Db⁴",
	60:"C⁴ (middle C)",
	59:"B³",
	58:"A#³ / Bb³",
	57:"A³",
	56:"G#³ / Ab³",
	55:"G³",
	54:"F#³ / Gb³",
	53:"F³",
	52:"E³",
	51:"D#³ / Eb³",
	50:"D³",
	49:"C#³ / Db³",
	48:"C³",
	47:"B²",
	46:"A#² / Bb²",
	45:"A²",
	44:"G#² / Ab²",
	43:"G²",
	42:"F#² / Gb²",
	41:"F²",
	40:"E²",
	39:"D#² / Eb²",
	38:"D²",
	37:"C#² / Db²",
	36:"C²",
	35:"B¹",
	34:"A#¹ / Bb¹",
	33:"A¹",
	32:"G#¹ / Ab¹",
	31:"G¹",
	30:"F#¹ / Gb¹",
	29:"F¹",
	28:"E¹",
	27:"D#¹ / Eb¹",
	26:"D¹",
	25:"C#¹ / Db¹",
	24:"C¹",
	23:"B0",
	22:"A#0 / Bb0",
	21:"A0",
	20:"G#",
	19:"G",
	18:"F#",
	17:"F",
	16:"E",
	15:"D#",
	14:"D",
	13:"C#",
	12:"C0",
	11:"B",
	10:"A#",
	9:"A",
	8:"G#",
	7:"G",
	6:"F#",
	5:"F",
	4:"E",
	3:"D#",
	2:"D",
	1:"C#",
	0:"C-¹"
}
