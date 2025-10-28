extends Control

@onready var note_scene = preload("res://scenes/type_game/note/note.tscn")
@onready var note_spawn = %NoteSpawn

enum GameMode {
	WHITE_NOTES_ONLY,
	ALL_NOTES,
}

const BASE_VOLIN_C_NOTE = 60

var spawn_range = 12
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

func _note_pressed(note: int) -> void:
	if spawned_notes.size() == 1 && spawned_notes.get(0).noteType == note:
		_spawn_note()

func generate_note() -> int:
	var note = BASE_VOLIN_C_NOTE
	
	if game_mode == GameMode.WHITE_NOTES_ONLY:
		var offset = randi_range(0, spawn_range)
		
		while is_black_note(offset):
			offset = randi_range(0, spawn_range)
		
		note += offset
	else:
		note += randi_range(0, spawn_range)
		
	return note

func is_black_note(note: int) -> bool:
	var base = note % 12
	return base == 1 || base == 3 || base == 6 || base == 8 || base == 10
