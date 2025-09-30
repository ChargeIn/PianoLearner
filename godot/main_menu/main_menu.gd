extends Control

var save_file_path = 'user:://save-game-0.0.0.save'
	
func _load_game():
	var save_file = FileAccess.open(save_file_path, FileAccess.READ)
	

func _ready():
	if !PianoManager.is_loaded:
		$VBoxContainer/Play.disabled = true
		$VBoxContainer/ErrorLabel.text = "Error: Could not load piano plugin :/"

func _on_play_pressed() -> void:
	PianoManager.start_game();
