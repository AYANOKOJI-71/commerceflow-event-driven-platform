import { describe, expect, it } from "vitest";
import { money, shortId } from "./format";

describe("operations formatters", () => {
  it("formats safe demo order values", () => {
    expect(money(3998)).toBe("$39.98");
    expect(shortId("c73e8bd1-a75c-43f8-b4e1-8b9a2fc8b9c2")).toBe("C73E8BD1");
  });
});
