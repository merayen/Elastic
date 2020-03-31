import pygame
from dataclasses import dataclass
import random
from typing import List, Set

pygame.init()
screen = pygame.display.set_mode((1000,1000))


@dataclass
class Node:
	inlets: int
	outlets: int
	x: float = 0
	y: float = 0
	xspeed: float = 0
	yspeed: float = 0

	def draw(self):
		pygame.draw.rect(screen, (255,255,255), (self.x, self.y, 100, 100))

		for i in range(self.inlets):
			pygame.draw.rect(screen, (0, 255, 0), (self.x - 5, self.y + 10 + 20 * i, 10, 10))
		for i in range(self.outlets):
			pygame.draw.rect(screen, (0, 255, 255), (self.x + 95, self.y + 10 + 20 * i, 10, 10))


@dataclass
class Line:
	node_a: Node
	node_a_port: int
	node_b: Node
	node_b_port: int

	def draw(self):	
		pygame.draw.line(screen, (255,0,255), (int(self.node_a.x) + 100, int(self.node_a.y) + 10 + 20 * self.node_a_port + 5), (int(self.node_b.x) + 100, int(self.node_b.y) + 10 + self.node_b_port * 20 + 5))

nodes = [Node(random.randrange(4), random.randrange(4), x = random.randrange(0, 900), y = random.randrange(0, 900)) for i in range(random.randrange(10,20))]
lines = []
for i in range(random.randrange(10,20)):
	node_a = random.choice(nodes)
	node_b = random.choice(nodes)

	if node_a is node_b:
		continue

	if not node_a.outlets or not node_b.inlets:
		continue

	available_inlets = set(range(node_b.inlets)).difference(line.node_b_port for line in lines if line.node_b is node_b)

	if not available_inlets:
		continue
	
	lines.append(Line(node_a, random.randrange(node_a.outlets), node_b, random.choice(list(available_inlets))))


@dataclass
class Ports:
	inlets: Set[int]
	outlets: Set[int]


def get_connections(node):
	return Ports(
		set(line.node_a_port for line in lines if line.node_a is node),
		set(line.node_b_port for line in lines if line.node_b is node)
	)

def solve_by_route(nodes, lines):
	# The left most nodes are those nodes that does not have any connected inputs
	left_most = [node for node in nodes if node.inlets and not get_connections(node).inlets]
	print(left_most)


solve_by_route(nodes, lines)

while not any(event.type == pygame.KEYDOWN for event in pygame.event.get()):
	for node in nodes:
		node.draw()
	
	for line in lines:
		line.draw()

	pygame.display.update()
	screen.fill((0,0,0))

pygame.quit()
