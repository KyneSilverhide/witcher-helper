import { Component } from '@angular/core';
import { Dice, ExpressionNode } from 'dice-typescript';
import { DiceResult } from 'dice-typescript/dist/interpreter';

@Component({
  selector: 'jhi-fallout-dices',
  templateUrl: './fallout-dices.component.html',
  styleUrls: ['./fallout-dices.component.scss'],
})
export class FalloutDicesComponent {
  d20Count = 1;
  combatDiceCount = 1;
  d20Results: DiceResult | null = null;
  d20Children: ExpressionNode[] = [];
  combatDiceChildren: ExpressionNode[] = [];

  constructor() {
    /**/
  }

  public rollD20(): void {
    const dice = new Dice();
    this.d20Results = dice.roll(String(this.d20Count) + 'd20');
    this.d20Children = [];
    this.d20Results.reducedExpression.forEachChild(child => {
      this.d20Children.push(child);
    });
  }

  public rollCombat(): void {
    const dice = new Dice();
    const combatDiceResults = dice.roll(String(this.combatDiceCount) + 'd6');
    this.combatDiceChildren = [];
    combatDiceResults.reducedExpression.forEachChild(child => {
      this.combatDiceChildren.push(child);
    });
  }
}
