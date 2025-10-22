@tool
extends Button

@export var label: String = "Button":
	set(value):
		label = value
		self.text = label

func _ready() -> void:
	self.text = label
