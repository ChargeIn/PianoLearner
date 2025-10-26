extends Control

@onready var noteScene = preload("res://scenes/type_game/note/note.tscn")
@onready var note_spawn = %NoteSpawn

var spawned_notes = []

func _ready() -> void:
	PianoManager.note_pressed.connect(_note_pressed)
	_spawrn_note()

func _spawrn_note() -> void:
	for n in spawned_notes:
		remove_child(n)
	
	var new_note = noteScene.instantiate()
	new_note.init(78)
	new_note.move(note_spawn.position.x - size.x / 2, note_spawn.position.y)
	call_deferred("add_child", new_note)
	spawned_notes.push_back(new_note)

func _note_pressed(note: int) -> void:
	if spawned_notes.size() == 1 && spawned_notes.get(0).noteType == note:
		_spawrn_note()
